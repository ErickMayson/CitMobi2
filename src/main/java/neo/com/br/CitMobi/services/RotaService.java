package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RotaService {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioService.class);

    private final LinhaRepository linhaRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;

    public RotaService(ItinerarioRepository itinerarioRepository, RotaRepository rotaRepository, LinhaRepository linhaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
        this.linhaRepository = linhaRepository;
    }

    public ResponseEntity<GenericResponse<List<RotaRecord>>> getRotasPerLine(String linha, String atendimento, Long municipio) {
        try {
            Optional<List<Rota>> optionalRotas = rotaRepository.getRotasPerLine(linha, atendimento, municipio);
            if(optionalRotas.isEmpty()) {
                logger.error("Nenhuma rota encontrada para essa linha.");
                GenericResponse<List<RotaRecord>> errorResponse = new GenericResponse<>("404", "Nenhuma rota encontrada para essa linha", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            List<Long> sentidos = optionalRotas.get().stream()
                    .sorted(Comparator.comparing(rota -> rota.getRotaId().getSequencia())) // Sort by sequencia
                    .map(rota -> rota.getRotaId().getItinerario())
                    .distinct()
                    .toList();

            List<RotaRecord> rotas = new ArrayList<>();

            for (Long itinerarioId : sentidos) {
                List<Parada> paradas = optionalRotas.get()
                        .stream()
                        .filter(rota -> rota.getRotaId().getItinerario().equals(itinerarioId))  // Ensure correct itinerary
                        .map(rota -> rota.getRotaId().getParada())
                        .toList();
                RotaRecord rotaRecord = new RotaRecord(itinerarioId, paradas);
                rotas.add(rotaRecord);
            }

            logger.error("Rotas encontradas: {}", rotas);
            GenericResponse<List<RotaRecord>> response = new GenericResponse<>("200", "Rotas encontradas", rotas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao procurar a rota", e);
            GenericResponse<List<RotaRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<List<RotaRecord>>> createRota() {
        try {
            GenericResponse<List<RotaRecord>> response = new GenericResponse<>("200", "Rotas encontradas", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao procurar a rota", e);
            GenericResponse<List<RotaRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
