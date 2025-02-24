package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.ItinerarioId;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// As API Graphhopper e a ORS(OpenRouteServiceRoutes) utilizam (LONGITUDE, LATITUDE), o padrao da API é (LATITUDE, LONGITUDE)


@Service
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(RotaService.class);

    private final ItinerarioRepository itinerarioRepository;
    private final LinhaRepository linhaRepository;
    private final ParadaService paradaService;
    private final ParadaRepository paradaRepository;
    private final RotaRepository rotaRepository;


    public ItinerarioService(ItinerarioRepository itinerarioRepository, LinhaRepository linhaRepository, ParadaService paradaService, ParadaRepository paradaRepository, RotaRepository rotaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.linhaRepository = linhaRepository;
        this.paradaService = paradaService;
        this.paradaRepository = paradaRepository;
        this.rotaRepository = rotaRepository;


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

    public ResponseEntity<GenericResponse<ItinerarioRecord>> createItinerario(ItinerarioRecord itinerario, String linha, String atendimento, String prefixo) {
        try {
            // Se o Java respeitar a ordem das paradas no JSON
            // Vou delegar as responsabilidades de manter a sequencia do JSON no frontend.

            if(itinerario.paradas().isEmpty()) {
                String message = String.format(
                        "Nao é possivel criar itinerarios vazios, tente adicionar paradas para a rota %s-%s %s",
                        linha, atendimento, prefixo
                );
                logger.error(message);
                GenericResponse<ItinerarioRecord> errorResponse = new GenericResponse<>("400", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            if(itinerario.paradas().size() == 1) {
                String message = String.format(
                        "Não é possível concluir essa rota, tente adicionar mais paradas para a rota %s-%s %s",
                        linha, atendimento, prefixo
                );
                logger.error(message);
                GenericResponse<ItinerarioRecord> errorResponse = new GenericResponse<>("400", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            List<ParadaRecord> paradasACriar = new ArrayList<>();
            Map<Integer, ParadaRecord> indexedParadas = new HashMap<>();

            AtomicInteger index = new AtomicInteger(0);
            itinerario.paradas().forEach(paradaRecord -> {
                int pos = index.getAndIncrement();
                if (paradaRecord.paradaId() == null) {
                    paradasACriar.add(paradaRecord);
                    indexedParadas.put(pos, null); // Placeholder for newly created parada
                } else {
                    indexedParadas.put(pos, paradaRecord);
                }
            });

            List<ParadaRecord> orderedParadas = createParadasForItinerario(paradasACriar, indexedParadas);
            logger.info("PARADAS: {}", orderedParadas);
            long sequencia = 1L;
            List<ParadaRecord> paradasItinerario = new ArrayList<>();
            for(ParadaRecord paradaRecord : orderedParadas) {
                Itinerario newPoint = new Itinerario(new ItinerarioId(itinerario.itinerarioId(), paradaRecord.toParada(), sequencia++));
                Itinerario itinerarioCriado = itinerarioRepository.save(newPoint);
                paradasItinerario.add(itinerarioCriado.itinerarioId.getParada().toRecord());
            }
            ItinerarioRecord rotaItinerario = new ItinerarioRecord(itinerario.itinerarioId(), paradasItinerario);


            GenericResponse<ItinerarioRecord> response = new GenericResponse<>("200", "Itinerario criado.", rotaItinerario);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao procurar a rota", e);
            GenericResponse<ItinerarioRecord> errorResponse = new GenericResponse<>("500", "Erro ao criar itinerario", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO
    // Reorganizar toda a sequencia ao adicionar ou remover paradas.
    // Se possivel, realizar isso no minimo de operacoes possiveis sem utilizar full deletes(apagar toda rota e criar do zero)
    //public ResponseEntity<GenericResponse<ItinerarioRecord>> editItinerario() {}

    private List<ParadaRecord> createParadasForItinerario(List<ParadaRecord> paradasACriar, Map<Integer, ParadaRecord> indexedParadas) {
        ResponseEntity<GenericResponse<List<GenericResponse<ParadaRecord>>>> responseCreateParadas =
                paradaService.createParadas(paradasACriar);

        // Extract response body
        GenericResponse<List<GenericResponse<ParadaRecord>>> body = responseCreateParadas.getBody();
        if (body == null || body.data() == null) {
            throw new RuntimeException("Error: Response from createParadas is null.");
        }

        // Separate success and failed records
        List<ParadaRecord> createdParadas = new ArrayList<>();
        List<GenericResponse<ParadaRecord>> failedParadas = new ArrayList<>();

        for (GenericResponse<ParadaRecord> recordResponse : body.data()) {
            if ("200".equals(recordResponse.status())) { // Check if status is HTTP 200
                createdParadas.add(recordResponse.data());
            } else {
                failedParadas.add(recordResponse);
            }
        }

        // If any parada failed, return an error response
        if (!failedParadas.isEmpty()) {
            throw new RuntimeException("Some paradas failed to be created: " + failedParadas);
        }

        // Restore order
        Iterator<ParadaRecord> createdIterator = createdParadas.iterator();
        indexedParadas.replaceAll((pos, parada) -> parada == null ? createdIterator.next() : parada);
        return new ArrayList<>(indexedParadas.values());
    }


}
