package neo.com.br.CitMobi.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.viagem.ViagemRecord;
import neo.com.br.CitMobi.models.records.viagem.ViagemRequest;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoStatus;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.veiculo.VeiculoRepository;
import neo.com.br.CitMobi.repository.viagem.ViagemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;
    private final TokenService tokenService;

    @Transactional
    public ResponseEntity<GenericResponse<ViagemRecord>> iniciarViagem(ViagemRequest request, String authHeader) {
        log.info("Iniciando nova viagem operacional: {}", request);

        // 1. Identificar e validar o motorista
        Usuario motorista = resolveMotorista(request.motoristaId(), authHeader);
        if (motorista == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "Motorista não encontrado ou inválido", null));
        }

        if (!"S".equalsIgnoreCase(motorista.getFlagAtivo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "Motorista está inativo no sistema", null));
        }

        // Verificar se motorista já possui viagem ativa
        Optional<Viagem> activeTripDriver = viagemRepository.findFirstByMotorista_IdAndStatus(motorista.getId(), ViagemStatus.EM_ANDAMENTO);
        if (activeTripDriver.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new GenericResponse<>("409", "Motorista já possui uma viagem em andamento (ID: " + activeTripDriver.get().getId() + ")", null));
        }

        // 2. Validar o veículo
        Optional<Veiculo> veiculoOpt = veiculoRepository.findById(request.veiculoId());
        if (veiculoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Veículo com ID " + request.veiculoId() + " não encontrado", null));
        }
        Veiculo veiculo = veiculoOpt.get();

        if (veiculo.getStatus() != VeiculoStatus.ATIVO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "Veículo não está apto para operação. Status atual: " + veiculo.getStatus(), null));
        }

        // Verificar se veículo já possui viagem ativa
        Optional<Viagem> activeTripVehicle = viagemRepository.findFirstByVeiculo_IdAndStatus(veiculo.getId(), ViagemStatus.EM_ANDAMENTO);
        if (activeTripVehicle.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new GenericResponse<>("409", "Veículo já está em operação em outra viagem ativa (ID: " + activeTripVehicle.get().getId() + ")", null));
        }

        // 3. Validar linha e rota
        Optional<Linha> linhaOpt = linhaRepository.findById(request.linhaId());
        if (linhaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Linha com ID " + request.linhaId() + " não encontrada", null));
        }
        Linha linha = linhaOpt.get();

        Optional<Rota> rotaOpt = rotaRepository.findById(request.rotaId());
        if (rotaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Rota com ID " + request.rotaId() + " não encontrada", null));
        }
        Rota rota = rotaOpt.get();

        if (rota.getLinha() == null || !rota.getLinha().getId().equals(linha.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "A rota selecionada não pertence à linha informada", null));
        }

        // 4. Criar e persistir a sessão de viagem
        Viagem viagem = new Viagem(motorista, veiculo, linha, rota);
        viagem.setDataInicio(OffsetDateTime.now());
        viagem.setStatus(ViagemStatus.EM_ANDAMENTO);

        Viagem saved = viagemRepository.save(viagem);
        log.info("Viagem iniciada com sucesso. ID: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>("201", "Viagem iniciada com sucesso", toRecord(saved)));
    }

    @Transactional
    public ResponseEntity<GenericResponse<ViagemRecord>> finalizarViagem(Long viagemId, String authHeader) {
        log.info("Finalizando viagem ID: {}", viagemId);

        Optional<Viagem> viagemOpt = viagemRepository.findById(viagemId);
        if (viagemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Viagem com ID " + viagemId + " não encontrada", null));
        }

        Viagem viagem = viagemOpt.get();
        if (viagem.getStatus() != ViagemStatus.EM_ANDAMENTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "A viagem já foi encerrada anteriormente com status: " + viagem.getStatus(), toRecord(viagem)));
        }

        viagem.setDataFim(OffsetDateTime.now());
        viagem.setStatus(ViagemStatus.FINALIZADA);

        Viagem saved = viagemRepository.save(viagem);
        log.info("Viagem {} finalizada com sucesso às {}", saved.getId(), saved.getDataFim());

        return ResponseEntity.ok(new GenericResponse<>("200", "Viagem finalizada com sucesso", toRecord(saved)));
    }

    @Transactional
    public ResponseEntity<GenericResponse<ViagemRecord>> cancelarViagem(Long viagemId, String authHeader) {
        log.info("Cancelando viagem ID: {}", viagemId);

        Optional<Viagem> viagemOpt = viagemRepository.findById(viagemId);
        if (viagemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>("404", "Viagem com ID " + viagemId + " não encontrada", null));
        }

        Viagem viagem = viagemOpt.get();
        if (viagem.getStatus() != ViagemStatus.EM_ANDAMENTO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponse<>("400", "A viagem não está em andamento. Status: " + viagem.getStatus(), toRecord(viagem)));
        }

        viagem.setDataFim(OffsetDateTime.now());
        viagem.setStatus(ViagemStatus.CANCELADA);

        Viagem saved = viagemRepository.save(viagem);
        log.info("Viagem {} cancelada com sucesso", saved.getId());

        return ResponseEntity.ok(new GenericResponse<>("200", "Viagem cancelada com sucesso", toRecord(saved)));
    }

    public ResponseEntity<GenericResponse<List<ViagemRecord>>> getViagensAtivas(Long operadorId, Long linhaId, String authHeader) {
        String role = tokenService.getRoleFromToken(authHeader);
        String flagRegulador = tokenService.getFlagReguladorFromToken(authHeader);
        String cnpjToken = tokenService.getOperadorIdFromToken(authHeader);

        List<Viagem> ativas = viagemRepository.findByStatus(ViagemStatus.EM_ANDAMENTO);

        List<ViagemRecord> filtered = ativas.stream()
                .filter(v -> {
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

                    return true;
                })
                .map(this::toRecord)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GenericResponse<>("200", "Viagens ativas encontradas", filtered));
    }

    public ResponseEntity<GenericResponse<ViagemRecord>> getViagemById(Long id) {
        return viagemRepository.findById(id)
                .map(v -> ResponseEntity.ok(new GenericResponse<>("200", "Viagem encontrada", toRecord(v))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>("404", "Viagem não encontrada", null)));
    }

    private Usuario resolveMotorista(UUID requestDriverId, String authHeader) {
        if (requestDriverId != null) {
            return usuarioRepository.findById(requestDriverId).orElse(null);
        }

        String login = tokenService.getLoginFromToken(authHeader);
        if (login != null) {
            return usuarioRepository.findFirstByLoginIgnoreCase(login).orElse(null);
        }

        return null;
    }

    public ViagemRecord toRecord(Viagem v) {
        if (v == null) return null;

        UUID motoristaId = v.getMotorista() != null ? v.getMotorista().getId() : null;
        String motoristaNome = v.getMotorista() != null ? v.getMotorista().getNome() : null;

        Long veiculoId = v.getVeiculo() != null ? v.getVeiculo().getId() : null;
        String veiculoPlaca = v.getVeiculo() != null ? v.getVeiculo().getPlaca() : null;
        String veiculoCodigo = v.getVeiculo() != null ? v.getVeiculo().getCodigoVeiculo() : null;

        Long operadorId = (v.getVeiculo() != null && v.getVeiculo().getOperador() != null)
                ? v.getVeiculo().getOperador().getId()
                : (v.getLinha() != null && v.getLinha().getOperador() != null ? v.getLinha().getOperador().getId() : null);

        String operadorNome = (v.getVeiculo() != null && v.getVeiculo().getOperador() != null)
                ? v.getVeiculo().getOperador().getRazaoSocial()
                : (v.getLinha() != null && v.getLinha().getOperador() != null ? v.getLinha().getOperador().getRazaoSocial() : null);

        Long linhaId = v.getLinha() != null ? v.getLinha().getId() : null;
        String linhaCodigo = v.getLinha() != null ? v.getLinha().getCodigoLinha() : null;
        String linhaDescricao = v.getLinha() != null ? v.getLinha().getDescricao() : null;

        Long rotaId = v.getRota() != null ? v.getRota().getId() : null;
        String rotaPrefixo = v.getRota() != null ? v.getRota().getPrefixo() : null;
        String sentido = v.getRota() != null ? v.getRota().getSentido() : null;

        return new ViagemRecord(
                v.getId(),
                motoristaId,
                motoristaNome,
                veiculoId,
                veiculoPlaca,
                veiculoCodigo,
                operadorId,
                operadorNome,
                linhaId,
                linhaCodigo,
                linhaDescricao,
                rotaId,
                rotaPrefixo,
                sentido,
                v.getDataInicio(),
                v.getDataFim(),
                v.getStatus() != null ? v.getStatus().name() : null
        );
    }
}
