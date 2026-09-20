package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.*;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.RotaResponse;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RotaService {

    private static final Logger logger = LoggerFactory.getLogger(RotaService.class);

    private final ItinerarioRepository itinerarioRepository;
    private final ItinerarioService itinerarioService;
    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;

    public RotaService(ItinerarioService itinerarioService,
                       ItinerarioRepository itinerarioRepository,
                       RotaRepository rotaRepository,
                       LinhaRepository linhaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.itinerarioService = itinerarioService;
        this.linhaRepository = linhaRepository;
        this.rotaRepository = rotaRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<RotaResponse>> getRota(String linha, String atendimento, String municipio) {
        try {
            Long municipioCod = Long.parseLong(municipio);
            Optional<Linha> optionalLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linha, atendimento, municipioCod
            );

            if (optionalLinha.isEmpty()) {
                String message = "A linha " + linha + "/" + atendimento + " não foi encontrada.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha linhaEntity = optionalLinha.get();
            List<Rota> rotas = rotaRepository.findByLinha_Id(linhaEntity.getId());
            if (rotas.isEmpty()) {
                String message = "A linha " + linha + "/" + atendimento + " não possui nenhuma rota cadastrada.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            List<RotaRecord> rotaRecords = new ArrayList<>();
            for (Rota rota : rotas) {
                List<Itinerario> itinerarios = itinerarioRepository.findByRota_IdOrderBySequenciaAsc(rota.getId());
                ItinerarioRecord itinerarioRecord = null;
                if (!itinerarios.isEmpty()) {
                    List<ParadaRecord> paradasRota = itinerarios.stream()
                            .map(it -> it.getParada().toRecord())
                            .toList();
                    itinerarioRecord = new ItinerarioRecord(rota.getId(), paradasRota);
                }
                RotaRecord rotaRecord = new RotaRecord(
                        rota.getId(),
                        linha,
                        atendimento,
                        rota.getPrefixo(),
                        municipioCod,
                        rota.getSentido(),
                        itinerarioRecord
                );
                rotaRecords.add(rotaRecord);
            }

            RotaResponse rotaResponse = new RotaResponse(rotaRecords);
            GenericResponse<RotaResponse> response = new GenericResponse<>("200", "Rotas encontradas", rotaResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<RotaResponse>> createRota(String linha, String atendimento, String municipio, RotaRecord novaRota) {
        try {
            Long municipioCod = Long.parseLong(municipio);
            Optional<Linha> optionalLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linha, atendimento, municipioCod
            );

            if (optionalLinha.isEmpty()) {
                String message = "A linha " + linha + "/" + atendimento + " não foi encontrada.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha linhaEntity = optionalLinha.get();
            Optional<Rota> existingRota = rotaRepository.findByLinha_IdAndSentido(linhaEntity.getId(), novaRota.linhaSentido());
            if (existingRota.isPresent()) {
                logger.info("A linha {}/{} já possui uma rota com o sentido {}. Atualizando rota e itinerário (upsert).",
                        linha, atendimento, novaRota.linhaSentido());
                return executeUpdateRota(linhaEntity, existingRota.get(), novaRota, municipioCod);
            }

            Rota rota = new Rota(linhaEntity, novaRota.prefixo().trim(), novaRota.linhaSentido().trim());
            rota = rotaRepository.save(rota);

            if (novaRota.itinerario() == null || novaRota.itinerario().paradas() == null || novaRota.itinerario().paradas().isEmpty()) {
                RotaRecord rotaCriada = new RotaRecord(
                        rota.getId(),
                        linha,
                        atendimento,
                        rota.getPrefixo(),
                        municipioCod,
                        rota.getSentido(),
                        null
                );
                GenericResponse<RotaResponse> response = new GenericResponse<>("201", "Nova rota criada com sucesso, tente adicionar um itinerario.", new RotaResponse(List.of(rotaCriada)));
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            ItinerarioRecord itinerarioReq = new ItinerarioRecord(rota.getId(), novaRota.itinerario().paradas());
            ResponseEntity<GenericResponse<ItinerarioRecord>> criaItinerario = itinerarioService.createItinerario(
                    itinerarioReq, linha, atendimento, novaRota.prefixo()
            );

            if (criaItinerario.getStatusCode().is2xxSuccessful() && criaItinerario.getBody() != null) {
                RotaRecord rotaCriada = new RotaRecord(
                        rota.getId(),
                        linha,
                        atendimento,
                        rota.getPrefixo(),
                        municipioCod,
                        rota.getSentido(),
                        criaItinerario.getBody().data()
                );
                GenericResponse<RotaResponse> response = new GenericResponse<>("201", "Rota criada com sucesso.", new RotaResponse(List.of(rotaCriada)));
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(new GenericResponse<>("500", "Erro ao criar o itinerario", null), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            logger.error("Erro ao criar rota: ", e);
            GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("500", "Erro ao criar rota: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<RotaResponse>> updateRota(String linha, String atendimento, String municipio, RotaRecord rotaRecord) {
        try {
            Long municipioCod = Long.parseLong(municipio);
            Optional<Linha> optionalLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linha, atendimento, municipioCod
            );

            if (optionalLinha.isEmpty()) {
                String message = "A linha " + linha + "/" + atendimento + " não foi encontrada.";
                logger.error(message);
                return new ResponseEntity<>(new GenericResponse<>("404", message, null), HttpStatus.NOT_FOUND);
            }

            Linha linhaEntity = optionalLinha.get();
            Optional<Rota> optionalRota = rotaRepository.findByLinha_IdAndSentido(linhaEntity.getId(), rotaRecord.linhaSentido());
            if (optionalRota.isEmpty()) {
                String message = "A rota com sentido " + rotaRecord.linhaSentido() + " não foi encontrada para a linha " + linha + "/" + atendimento + ".";
                logger.error(message);
                return new ResponseEntity<>(new GenericResponse<>("404", message, null), HttpStatus.NOT_FOUND);
            }

            return executeUpdateRota(linhaEntity, optionalRota.get(), rotaRecord, municipioCod);
        } catch (Exception e) {
            logger.error("Erro ao atualizar rota: ", e);
            return new ResponseEntity<>(new GenericResponse<>("500", "Erro ao atualizar rota: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<RotaResponse>> updateRotaById(Long id, RotaRecord rotaRecord) {
        try {
            Optional<Rota> optionalRota = rotaRepository.findById(id);
            if (optionalRota.isEmpty()) {
                String message = "Rota não encontrada com ID: " + id;
                logger.error(message);
                return new ResponseEntity<>(new GenericResponse<>("404", message, null), HttpStatus.NOT_FOUND);
            }

            Rota rota = optionalRota.get();
            Linha linha = rota.getLinha();
            Long municipioCod = linha.getMunicipio() != null ? linha.getMunicipio().getCodigoIbge() : null;
            return executeUpdateRota(linha, rota, rotaRecord, municipioCod);
        } catch (Exception e) {
            logger.error("Erro ao atualizar rota por ID: ", e);
            return new ResponseEntity<>(new GenericResponse<>("500", "Erro ao atualizar rota: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<GenericResponse<RotaResponse>> executeUpdateRota(Linha linhaEntity, Rota rota, RotaRecord rotaRecord, Long municipioCod) {
        if (rotaRecord.prefixo() != null && !rotaRecord.prefixo().isBlank()) {
            rota.setPrefixo(rotaRecord.prefixo().trim());
        }
        if (rotaRecord.linhaSentido() != null && !rotaRecord.linhaSentido().isBlank()) {
            rota.setSentido(rotaRecord.linhaSentido().trim());
        }
        rota = rotaRepository.save(rota);

        ItinerarioRecord itinerarioResult = null;
        if (rotaRecord.itinerario() != null) {
            if (rotaRecord.itinerario().paradas() != null && !rotaRecord.itinerario().paradas().isEmpty()) {
                ResponseEntity<GenericResponse<ItinerarioRecord>> replaceResp = itinerarioService.replaceItinerario(
                        rota, rotaRecord.itinerario().paradas(), linhaEntity.getCodigoLinha(), linhaEntity.getAtendimento(), rota.getPrefixo()
                );
                if (!replaceResp.getStatusCode().is2xxSuccessful() || replaceResp.getBody() == null) {
                    return new ResponseEntity<>(new GenericResponse<>("500", "Erro ao atualizar itinerário da rota.", null), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                itinerarioResult = replaceResp.getBody().data();
            } else {
                itinerarioRepository.deleteByRota_Id(rota.getId());
                itinerarioRepository.flush();
            }
        } else {
            // Keep existing itinerario if field was omitted
            List<Itinerario> existingItinerarios = itinerarioRepository.findByRota_IdOrderBySequenciaAsc(rota.getId());
            if (!existingItinerarios.isEmpty()) {
                List<ParadaRecord> paradasRota = existingItinerarios.stream()
                        .map(it -> it.getParada().toRecord())
                        .toList();
                itinerarioResult = new ItinerarioRecord(rota.getId(), paradasRota);
            }
        }

        RotaRecord updatedRecord = new RotaRecord(
                rota.getId(),
                linhaEntity.getCodigoLinha(),
                linhaEntity.getAtendimento(),
                rota.getPrefixo(),
                municipioCod,
                rota.getSentido(),
                itinerarioResult
        );
        GenericResponse<RotaResponse> response = new GenericResponse<>("200", "Rota e itinerário atualizados com sucesso.", new RotaResponse(List.of(updatedRecord)));
        return ResponseEntity.ok(response);
    }
}
