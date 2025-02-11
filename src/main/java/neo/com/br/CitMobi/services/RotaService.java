package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RotaService {

    private static final Logger logger = LoggerFactory.getLogger(RotaService.class);

    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;
    private final ItinerarioRepository itinerarioRepository;

    public RotaService(RotaRepository rotaRepository, ItinerarioRepository itinerarioRepository, LinhaRepository linhaRepository) {
        this.rotaRepository = rotaRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.linhaRepository = linhaRepository;
    }

    public ResponseEntity<GenericResponse<RotaResponse>> getRota(String linha, String atendimento, String municipio) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");
            //Optional<List<Rota>> itinerario = rotaRepository.findById();

            Optional<List<Rota>> rotas = rotaRepository.getRotaIdByLinhaId(linha, atendimento, Long.parseLong(municipio));
            if (rotas.isPresent() && rotas.get().isEmpty()) {
                String message = "A linha " + linha + "/" + atendimento + " não possui nenhuma rota cadastrada.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            RotaResponse itinerarioResponse = new RotaResponse(new ArrayList<>());
            rotas.get().forEach(rota -> {
                Optional<List<Itinerario>> optRota = itinerarioRepository.getRotaByItinerarioId(rota.getRotaId());
                if(optRota.isEmpty()) {
                    logger.error("A linha " + linha + "/" + atendimento + " não possui itinerario cadastrado" );
                    RotaRecord rotaRecord = new RotaRecord(linha, atendimento, rota.getPrefixo(), Long.parseLong(municipio), rota.getLinhaSentido(), null);
                    itinerarioResponse.rotaRecords().add(rotaRecord);
                } else {
                    List<Parada> paradasRota = new ArrayList<>();
                    optRota.get().forEach( toBeItinerario -> {
                        paradasRota.add(toBeItinerario.getItinerarioId().getParada());
                    });
                    ItinerarioRecord rotaResponse = new ItinerarioRecord(rota.getRotaId(), paradasRota);
                    logger.info("Adicionando itinerario...");
                    RotaRecord rotaRecord = new RotaRecord(linha, atendimento, rota.getPrefixo(), Long.parseLong(municipio), rota.getLinhaSentido(), rotaResponse);
                    itinerarioResponse.rotaRecords().add(rotaRecord);
                }
            });
            GenericResponse<RotaResponse> response = new GenericResponse<>("201", "Rota encontrado", itinerarioResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha, certifique-se que a linha existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<RotaResponse>> createRota(String linha, String atendimento, String municipio, RotaRecord novaRota) {
        try {
            if (doesLinhaExists(linha, atendimento, municipio)) {
                String message = "A linha " + linha + "/" + atendimento + " não foi encontrada.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            if (doesRotaExists(novaRota.linhaId(), novaRota.linhaAtendimento(), novaRota.municipio(), novaRota.linhaSentido())) {
                String message = "A linha " + linha + "/" + atendimento + " ja possui uma rota com esse destino.";
                logger.error(message);
                GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("409", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            if (novaRota.rota() == null) {
                Rota criarRota = novaRota.toRotaNoItinerario();
                rotaRepository.save(criarRota);
                GenericResponse<RotaResponse> response = new GenericResponse<>("201", "Nova rota criada com sucesso", new RotaResponse(Collections.singletonList(novaRota)));
                return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
            }
            Rota criarRota = novaRota.toRotaNoItinerario();
            rotaRepository.save(criarRota);


            GenericResponse<RotaResponse> response = new GenericResponse<>("201", "Placeholder.", new RotaResponse(Collections.singletonList(novaRota)));
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<RotaResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar o itinerario", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean doesLinhaExists(String linha, String atendimento, String municipio) {
        return linhaRepository.existsById(new LinhaId(linha, atendimento, Long.parseLong(municipio)));
    }

    private boolean doesRotaExists(String linha, String atendimento, Long municipio, String sentido) {
        return rotaRepository.doesRotaExists(linha, atendimento, municipio, sentido);
    }
}
