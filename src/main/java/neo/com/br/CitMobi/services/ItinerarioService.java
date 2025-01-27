package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.ItinerarioId;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.ItinerarioResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);

    private final ItinerarioRepository itinerarioRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository) {
        this.itinerarioRepository = itinerarioRepository;
    }


    public ResponseEntity<GenericResponse<ItinerarioResponse>> getItinerario(String linha, String atendimento, String municipio) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");

            //Optional<List<Itinerario>> itinerario = itinerarioRepository.findById();
            Optional<Itinerario> itinerario = Optional.of(new Itinerario());
            if (itinerario.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "";
                message = "A linha " + itinerario + "/" + atendimento + " não existe.";
                GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else {
                GenericResponse<ItinerarioResponse> response = new GenericResponse<>("201", "Linha encontrada", null);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<ItinerarioResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha, certifique-se que a linha existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
