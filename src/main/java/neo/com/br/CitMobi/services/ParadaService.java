package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParadaService {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioService.class);

    private final LinhaRepository linhaRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;
    private final ParadaRepository paradaRepository;

    public ParadaService(ItinerarioRepository itinerarioRepository, RotaRepository rotaRepository, LinhaRepository linhaRepository, ParadaRepository paradaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
        this.linhaRepository = linhaRepository;
        this.paradaRepository = paradaRepository;
    }

    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadasByLogradouro(String logradouro, Long municipio) {
        try {
            Optional<List<Parada>> optionalParadas = paradaRepository.findByLogradouro(logradouro, municipio);
            if(optionalParadas.isEmpty()) {
                logger.error("Nenhuma parada encontrada para esse endereço.");
                GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("404", "Nenhuma par`1ada encontrada para esse endereço.", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            List<ParadaRecord> paradas = optionalParadas.get()
                    .stream()
                    .map(Parada::toRecord)
                    .toList(); // Converts the stream into a List
            logger.error("Paradas encontradas: {}", paradas);
            GenericResponse<List<ParadaRecord>> response = new GenericResponse<>("200", "Nenhuma parada encontrada para esse endereço.", paradas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao procurar a parada", e);
            GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
