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
            if (rotaRepository.existsByLinha_IdAndSentido(linhaEntity.getId(), novaRota.linhaSentido())) {
                String message = "A linha " + linha + "/" + atendimento + " ja possui uma rota com o sentido " + novaRota.linhaSentido() + ".";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("409", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
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
}
