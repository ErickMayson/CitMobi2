package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.ItinerarioResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
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

    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, RotaRepository rotaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
    }

    public ResponseEntity<GenericResponse<ItinerarioResponse>> getItinerario(String linha, String atendimento, String municipio) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");
            //Optional<List<Itinerario>> itinerario = itinerarioRepository.findById();

            Optional<List<Itinerario>> itinerarios = itinerarioRepository.getItinerarioIdByLinhaId(linha, atendimento, Long.parseLong(municipio));
            if (itinerarios.isPresent() && itinerarios.get().isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "A linha " + linha + "/" + atendimento + " não possui nenhum itinerario cadastrado.";
                GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            ItinerarioResponse itinerarioResponse = new ItinerarioResponse(new ArrayList<>());
            itinerarios.get().forEach(itinerario -> {
                Optional<Rota> optRota = rotaRepository.getRotaByItinerarioId(itinerario.getItinerarioId());
                if(optRota.isEmpty()) {
                    logger.error("Itinerario da linha " + linha + "/" + atendimento + " não possui rota cadastrada." );
                    ItinerarioRecord itinerarioRecord = new ItinerarioRecord(linha, atendimento, itinerario.getPrefixo(), Long.parseLong(municipio), itinerario.getLinhaSentido(), null);
                    itinerarioResponse.itinerarioRecords().add(itinerarioRecord);
                } else {
                    logger.info("Adicionando itinerario...");
                    ItinerarioRecord itinerarioRecord = new ItinerarioRecord(linha, atendimento, itinerario.getPrefixo(), Long.parseLong(municipio), itinerario.getLinhaSentido(), optRota.get());
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
}
