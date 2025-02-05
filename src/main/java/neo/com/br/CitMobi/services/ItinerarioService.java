package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.*;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.request.ItinerarioRequest;
import neo.com.br.CitMobi.models.records.response.CriarItinerarioResponse;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.ItinerarioResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
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
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioService.class);

    private final LinhaRepository linhaRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, RotaRepository rotaRepository, LinhaRepository linhaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
        this.linhaRepository = linhaRepository;
    }

    public ResponseEntity<GenericResponse<ItinerarioResponse>> getItinerario(String linha, String atendimento, String municipio) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");
            //Optional<List<Itinerario>> itinerario = itinerarioRepository.findById();

            Optional<List<Itinerario>> itinerarios = itinerarioRepository.getItinerarioIdByLinhaId(linha, atendimento, Long.parseLong(municipio));
            if (itinerarios.isPresent() && itinerarios.get().isEmpty()) {
                logger.error("Essa linha não possui itinerarios");
                String message = "A linha " + linha + "/" + atendimento + " não possui nenhum itinerario cadastrado.";
                GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            ItinerarioResponse itinerarioResponse = new ItinerarioResponse(new ArrayList<>());
            itinerarios.get().forEach(itinerario -> {
                Optional<List<Rota>> optRota = rotaRepository.getRotaByItinerarioId(itinerario.getItinerarioId());
                if(optRota.isEmpty()) {
                    logger.error("Itinerario da linha " + linha + "/" + atendimento + " não possui rota cadastrada." );
                    ItinerarioRecord itinerarioRecord = new ItinerarioRecord(linha, atendimento, itinerario.getPrefixo(), Long.parseLong(municipio), itinerario.getLinhaSentido(), null);
                    itinerarioResponse.itinerarioRecords().add(itinerarioRecord);
                } else {
                    List<Parada> paradasRota = new ArrayList<>();
                    optRota.get().forEach( rota -> {
                        paradasRota.add(rota.getRotaId().getParada());
                    });
                    RotaRecord rotaResponse = new RotaRecord(itinerario.getItinerarioId(), paradasRota);
                    logger.info("Adicionando itinerario...");
                    ItinerarioRecord itinerarioRecord = new ItinerarioRecord(linha, atendimento, itinerario.getPrefixo(), Long.parseLong(municipio), itinerario.getLinhaSentido(), rotaResponse);
                    itinerarioResponse.itinerarioRecords().add(itinerarioRecord);
                }
            });
            GenericResponse<ItinerarioResponse> response = new GenericResponse<>("201", "Itinerario encontrado", itinerarioResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha, certifique-se que a linha existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<ItinerarioResponse>> createItinerario(String linha, String atendimento, String municipio, ItinerarioRecord novoItinerario) {
        try {
            if (doesLinhaExists(linha, atendimento, municipio)) {
                logger.error("Linha não encontrada");
                String message = "A linha " + linha + "/" + atendimento + " não foi encontrada.";
                GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            if (doesItinerarioExists(novoItinerario.linhaId(), novoItinerario.linhaAtendimento(), novoItinerario.municipio(), novoItinerario.linhaSentido())) {
                logger.error("Esse itinerario ja existe.");
                String message = "A linha " + linha + "/" + atendimento + " ja possui itinerario neste sentido.";
                GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("409", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            if (novoItinerario.rota() == null) {
                Itinerario criarItinerario = novoItinerario.toItinerarioNoRoute();
                itinerarioRepository.save(criarItinerario);
                GenericResponse<ItinerarioResponse> response = new GenericResponse<>("201", "Itinerario criado com sucesso", new ItinerarioResponse(Collections.singletonList(novoItinerario)));
                return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
            }
            // Para criar o Itinerario com a rota, preciso criar o service de Rota.
            GenericResponse<ItinerarioResponse> response = new GenericResponse<>("201", "Placeholder.", new ItinerarioResponse(Collections.singletonList(novoItinerario)));
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar o itinerario", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean doesLinhaExists(String linha, String atendimento, String municipio) {
        return linhaRepository.existsById(new LinhaId(linha, atendimento, Long.parseLong(municipio)));
    }

    private boolean doesItinerarioExists(String linha, String atendimento, Long municipio, String sentido) {
        return itinerarioRepository.doesItinerarioExists(linha, atendimento, municipio, sentido);
    }
}
