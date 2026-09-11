package neo.com.br.CitMobi.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaHistoricoRecord;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaPingRequest;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaVeiculoRecord;
import neo.com.br.CitMobi.models.telemetria.Telemetria;
import neo.com.br.CitMobi.models.telemetria.TelemetriaHistorico;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.telemetria.TelemetriaHistoricoRepository;
import neo.com.br.CitMobi.repository.telemetria.TelemetriaRepository;
import neo.com.br.CitMobi.repository.viagem.ViagemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final TelemetriaHistoricoRepository telemetriaHistoricoRepository;
    private final ViagemRepository viagemRepository;
    private final ParadaRepository paradaRepository;
    private final TokenService tokenService;

    @Transactional
    public ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> registrarPing(TelemetriaPingRequest request) {
        log.debug("Recebendo ping de telemetria: {}", request);

        // 1. Validar viagem ativa
        Optional<Viagem> viagemOpt = viagemRepository.findById(request.viagemId());
        if (viagemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Viagem com ID " + request.viagemId() + " não encontrada", null));
        }

        Viagem viagem = viagemOpt.get();
        if (viagem.getStatus() != ViagemStatus.EM_ANDAMENTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "A viagem informada não está em andamento (Status: " + viagem.getStatus() + ")", null));
        }

        Veiculo veiculo = viagem.getVeiculo();
        if (veiculo == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Viagem não possui veículo associado", null));
        }

        // 2. Resolver parada atual se informada
        Parada parada = null;
        if (request.paradaId() != null) {
            parada = paradaRepository.findById(request.paradaId()).orElse(null);
        }

        OffsetDateTime now = OffsetDateTime.now();
        BigDecimal velocidade = request.velocidade() != null ? request.velocidade() : BigDecimal.ZERO;
        BigDecimal bearing = request.bearing() != null ? request.bearing() : BigDecimal.ZERO;
        BigDecimal odometer = request.odometer() != null ? request.odometer() : BigDecimal.ZERO;

        // 3. UPSERT no cache de tempo real (T_VEI_TELEMETRIA)
        Telemetria telemetria = telemetriaRepository.findByVeiculoId(veiculo.getId())
                .orElseGet(() -> {
                    Telemetria nova = new Telemetria();
                    nova.setVeiculoId(veiculo.getId());
                    nova.setVeiculo(veiculo);
                    return nova;
                });

        telemetria.setViagem(viagem);
        telemetria.setLatitude(request.latitude());
        telemetria.setLongitude(request.longitude());
        telemetria.setVelocidade(velocidade);
        telemetria.setBearing(bearing);
        telemetria.setOdometer(odometer);
        telemetria.setSequenciaParadaAtual(request.sequenciaParadaAtual());
        telemetria.setStatusParadaAtual(request.statusParadaAtual());
        telemetria.setParadaAtual(parada);
        telemetria.setUltimaAtualizacao(now);

        Telemetria savedTelemetria = telemetriaRepository.save(telemetria);

        // 4. Inserção no ledger histórico (T_VEI_TELEMETRIA_HISTORICO)
        TelemetriaHistorico historico = new TelemetriaHistorico();
        historico.setViagem(viagem);
        historico.setLatitude(request.latitude());
        historico.setLongitude(request.longitude());
        historico.setVelocidade(velocidade);
        historico.setBearing(bearing);
        historico.setOdometer(odometer);
        historico.setDataRegistro(now);

        telemetriaHistoricoRepository.save(historico);

        return ResponseEntity.ok(new GenericResponse<>("200", "Telemetria registrada com sucesso", toRecord(savedTelemetria)));
    }

    public ResponseEntity<GenericResponse<List<TelemetriaVeiculoRecord>>> getVeiculosAtivos(
            Long linhaId,
            Long operadorId,
            Long municipioCod,
            String authHeader) {

        String role = tokenService.getRoleFromToken(authHeader);
        String flagRegulador = tokenService.getFlagReguladorFromToken(authHeader);
        String cnpjToken = tokenService.getOperadorIdFromToken(authHeader);

        List<Telemetria> ativas = telemetriaRepository.findByViagem_Status(ViagemStatus.EM_ANDAMENTO);

        List<TelemetriaVeiculoRecord> filtered = ativas.stream()
                .filter(t -> {
                    Viagem v = t.getViagem();
                    if (v == null) return false;

                    // Filtro de operador
                    if (operadorId != null) {
                        if (v.getLinha() == null || v.getLinha().getOperador() == null || !v.getLinha().getOperador().getId().equals(operadorId)) {
                            return false;
                        }
                    } else if (!"ROLE_ADMIN".equalsIgnoreCase(role) && !"S".equalsIgnoreCase(flagRegulador)) {
                        if (cnpjToken != null && (v.getLinha() == null || v.getLinha().getOperador() == null || !cnpjToken.equals(v.getLinha().getOperador().getCnpj()))) {
                            return false;
                        }
                    }

                    // Filtro de linha
                    if (linhaId != null) {
                        if (v.getLinha() == null || !v.getLinha().getId().equals(linhaId)) {
                            return false;
                        }
                    }

                    // Filtro de município
                    if (municipioCod != null) {
                        if (v.getLinha() == null || v.getLinha().getMunicipio() == null || !v.getLinha().getMunicipio().getCodigoIbge().equals(municipioCod)) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(this::toRecord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GenericResponse<>("200", "Posições em tempo real encontradas", filtered));
    }

    public ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> getVeiculoPosicao(Long veiculoId) {
        return telemetriaRepository.findByVeiculoId(veiculoId)
                .map(t -> ResponseEntity.ok(new GenericResponse<>("200", "Posição do veículo encontrada", toRecord(t))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Telemetria não encontrada para o veículo " + veiculoId, null)));
    }

    public ResponseEntity<GenericResponse<List<TelemetriaHistoricoRecord>>> getHistoricoViagem(Long viagemId) {
        List<TelemetriaHistorico> historico = telemetriaHistoricoRepository.findByViagem_IdOrderByDataRegistroAsc(viagemId);
        List<TelemetriaHistoricoRecord> records = historico.stream()
                .map(h -> new TelemetriaHistoricoRecord(
                        h.getId(),
                        h.getViagem() != null ? h.getViagem().getId() : null,
                        h.getLatitude(),
                        h.getLongitude(),
                        h.getVelocidade(),
                        h.getBearing(),
                        h.getOdometer(),
                        h.getDataRegistro()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GenericResponse<>("200", "Histórico de trajetória recuperado", records));
    }

    public TelemetriaVeiculoRecord toRecord(Telemetria t) {
        if (t == null) return null;

        Veiculo v = t.getVeiculo();
        Viagem viagem = t.getViagem();

        Long veiculoId = v != null ? v.getId() : t.getVeiculoId();
        String placa = v != null ? v.getPlaca() : null;
        String codigoVeiculo = v != null ? v.getCodigoVeiculo() : null;

        Long operadorId = (v != null && v.getOperador() != null)
                ? v.getOperador().getId()
                : (viagem != null && viagem.getLinha() != null && viagem.getLinha().getOperador() != null ? viagem.getLinha().getOperador().getId() : null);

        String operadorNome = (v != null && v.getOperador() != null)
                ? v.getOperador().getRazaoSocial()
                : (viagem != null && viagem.getLinha() != null && viagem.getLinha().getOperador() != null ? viagem.getLinha().getOperador().getRazaoSocial() : null);

        Long viagemId = viagem != null ? viagem.getId() : null;
        UUID motoristaId = (viagem != null && viagem.getMotorista() != null) ? viagem.getMotorista().getId() : null;
        String motoristaNome = (viagem != null && viagem.getMotorista() != null) ? viagem.getMotorista().getNome() : null;

        Long linhaId = (viagem != null && viagem.getLinha() != null) ? viagem.getLinha().getId() : null;
        String linhaCodigo = (viagem != null && viagem.getLinha() != null) ? viagem.getLinha().getCodigoLinha() : null;
        String linhaDescricao = (viagem != null && viagem.getLinha() != null) ? viagem.getLinha().getDescricao() : null;

        Long rotaId = (viagem != null && viagem.getRota() != null) ? viagem.getRota().getId() : null;
        String rotaPrefixo = (viagem != null && viagem.getRota() != null) ? viagem.getRota().getPrefixo() : null;
        String sentido = (viagem != null && viagem.getRota() != null) ? viagem.getRota().getSentido() : null;

        Long paradaAtualId = t.getParadaAtual() != null ? t.getParadaAtual().getId() : null;
        String paradaAtualLogradouro = t.getParadaAtual() != null ? t.getParadaAtual().getLogradouro() : null;

        return new TelemetriaVeiculoRecord(
                veiculoId,
                placa,
                codigoVeiculo,
                operadorId,
                operadorNome,
                viagemId,
                motoristaId,
                motoristaNome,
                linhaId,
                linhaCodigo,
                linhaDescricao,
                rotaId,
                rotaPrefixo,
                sentido,
                t.getLatitude(),
                t.getLongitude(),
                t.getVelocidade(),
                t.getBearing(),
                t.getOdometer(),
                t.getSequenciaParadaAtual(),
                t.getStatusParadaAtual(),
                paradaAtualId,
                paradaAtualLogradouro,
                t.getUltimaAtualizacao()
        );
    }
}
