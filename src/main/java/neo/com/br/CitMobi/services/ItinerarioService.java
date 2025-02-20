package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// As API Graphhopper e a ORS(OpenRouteServiceRoutes) utilizam (LONGITUDE, LATITUDE), o padrao da API é (LATITUDE, LONGITUDE)


@Service
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(RotaService.class);

    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;
    private final ItinerarioRepository itinerarioRepository;

    public ItinerarioService(RotaRepository rotaRepository, ItinerarioRepository itinerarioRepository, LinhaRepository linhaRepository) {
        this.rotaRepository = rotaRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.linhaRepository = linhaRepository;
    }

    public ResponseEntity<GenericResponse<List<ItinerarioRecord>>> getItinerariosPerLine(String linha, String atendimento, Long municipio) {
        try {
            Optional<List<Itinerario>> optionalRotas = itinerarioRepository.getItinerariosPerLine(linha, atendimento, municipio);
            if(optionalRotas.isEmpty()) {
                logger.error("Nenhuma rota encontrada para essa linha.");
                GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("404", "Nenhuma rota encontrada para essa linha", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            List<Long> sentidos = optionalRotas.get().stream()
                    .sorted(Comparator.comparing(rota -> rota.getItinerarioId().getSequencia())) // Sort by sequencia
                    .map(rota -> rota.getItinerarioId().getRotaId())
                    .distinct()
                    .toList();

            List<ItinerarioRecord> rotas = new ArrayList<>();

            for (Long rotaId : sentidos) {
                List<ParadaRecord> paradas = optionalRotas.get()
                        .stream()
                        .filter(rota -> rota.getItinerarioId().getRotaId().equals(rotaId))  // Ensure correct itinerary
                        .map(rota -> rota.getItinerarioId().getParada().toRecord())
                        .toList();
                ItinerarioRecord itinerarioRecord = new ItinerarioRecord(rotaId, paradas);
                rotas.add(itinerarioRecord);
            }

            logger.info("Itinerarios encontrados: {}", rotas);
            GenericResponse<List<ItinerarioRecord>> response = new GenericResponse<>("200", "Itinerarios encontrados", rotas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao procurar o itinerario dessa rota", e);
            GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<List<ItinerarioRecord>>> createItinerario(RotaRecord rota) {
        try {
            // Se o Java respeitar a ordem das paradas no JSON
            // Vou delegar as responsabilidades de manter a sequencia do JSON no frontend.

            if(rota.itinerario().paradas().isEmpty()) {
                String message = String.format(
                        "Nao é possivel criar itinerarios vazios, tente adicionar paradas para a rota %s-%s %s",
                        rota.linhaId(), rota.linhaAtendimento(), rota.prefixo()
                );
                logger.error(message);
                GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("400", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            if(rota.itinerario().paradas().size() == 1) {
                String message = String.format(
                        "Não é possível concluir essa rota, tente adicionar mais paradas para a rota %s-%s %s",
                        rota.linhaId(), rota.linhaAtendimento(), rota.prefixo()
                );
                logger.error(message);
                GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("400", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }


            GenericResponse<List<ItinerarioRecord>> response = new GenericResponse<>("200", "Itinerarios encontrados", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao procurar a rota", e);
            GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
