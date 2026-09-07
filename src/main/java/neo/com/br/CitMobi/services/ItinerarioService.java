package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItinerarioService {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioService.class);

    private final ItinerarioRepository itinerarioRepository;
    private final LinhaRepository linhaRepository;
    private final ParadaService paradaService;
    private final ParadaRepository paradaRepository;
    private final RotaRepository rotaRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository,
                             LinhaRepository linhaRepository,
                             ParadaService paradaService,
                             ParadaRepository paradaRepository,
                             RotaRepository rotaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.linhaRepository = linhaRepository;
        this.paradaService = paradaService;
        this.paradaRepository = paradaRepository;
        this.rotaRepository = rotaRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<ItinerarioRecord>>> getItinerariosPerLine(String linha, String atendimento, Long municipio) {
        try {
            Optional<Linha> optionalLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linha, atendimento, municipio
            );

            if (optionalLinha.isEmpty()) {
                logger.error("Nenhuma linha encontrada.");
                GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>(
                        "404", "Nenhuma linha encontrada.", null
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha linhaEntity = optionalLinha.get();
            List<Itinerario> itinerarios = itinerarioRepository.findByRota_Linha_IdOrderByRota_SentidoAscSequenciaAsc(linhaEntity.getId());

            if (itinerarios.isEmpty()) {
                logger.error("Nenhum itinerario encontrado para essa linha.");
                GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>(
                        "404", "Nenhum itinerario encontrado para essa linha.", null
                );
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Map<Long, List<ParadaRecord>> groupedByRota = new LinkedHashMap<>();
            for (Itinerario it : itinerarios) {
                Long rotaId = it.getRota().getId();
                groupedByRota.computeIfAbsent(rotaId, k -> new ArrayList<>())
                        .add(it.getParada().toRecord());
            }

            List<ItinerarioRecord> records = new ArrayList<>();
            groupedByRota.forEach((rotaId, paradas) -> records.add(new ItinerarioRecord(rotaId, paradas)));

            return ResponseEntity.ok(new GenericResponse<>("200", "Itinerarios encontrados", records));

        } catch (Exception e) {
            logger.error("Erro ao procurar o itinerario dessa rota", e);
            GenericResponse<List<ItinerarioRecord>> errorResponse = new GenericResponse<>("500", "Erro interno: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<ItinerarioRecord>> createItinerario(ItinerarioRecord itinerario, String linha, String atendimento, String prefixo) {
        try {
            if (itinerario.paradas() == null || itinerario.paradas().isEmpty()) {
                String message = String.format(
                        "Nao é possivel criar itinerarios vazios para a rota %s-%s %s",
                        linha, atendimento, prefixo
                );
                logger.error(message);
                return new ResponseEntity<>(new GenericResponse<>("400", message, null), HttpStatus.BAD_REQUEST);
            }

            if (itinerario.paradas().size() == 1) {
                String message = String.format(
                        "Não é possível concluir essa rota com apenas 1 parada para a rota %s-%s %s",
                        linha, atendimento, prefixo
                );
                logger.error(message);
                return new ResponseEntity<>(new GenericResponse<>("400", message, null), HttpStatus.BAD_REQUEST);
            }

            Optional<Rota> optionalRota = rotaRepository.findById(itinerario.itinerarioId());
            if (optionalRota.isEmpty()) {
                return new ResponseEntity<>(new GenericResponse<>("404", "Rota não encontrada com id: " + itinerario.itinerarioId(), null), HttpStatus.NOT_FOUND);
            }
            Rota rota = optionalRota.get();

            List<ParadaRecord> paradasACriar = new ArrayList<>();
            Map<Integer, ParadaRecord> indexedParadas = new HashMap<>();

            AtomicInteger index = new AtomicInteger(0);
            itinerario.paradas().forEach(paradaRecord -> {
                int pos = index.getAndIncrement();
                if (paradaRecord.paradaId() == null) {
                    paradasACriar.add(paradaRecord);
                    indexedParadas.put(pos, null);
                } else {
                    indexedParadas.put(pos, paradaRecord);
                }
            });

            List<ParadaRecord> orderedParadas = createParadasForItinerario(paradasACriar, indexedParadas);
            int sequencia = 1;
            List<ParadaRecord> paradasItinerario = new ArrayList<>();

            for (ParadaRecord paradaRecord : orderedParadas) {
                Parada parada = paradaRepository.findById(paradaRecord.paradaId())
                        .orElseGet(() -> paradaRepository.save(paradaRecord.toParada()));

                Itinerario newPoint = new Itinerario(rota, parada, sequencia++);
                newPoint = itinerarioRepository.save(newPoint);
                paradasItinerario.add(newPoint.getParada().toRecord());
            }

            ItinerarioRecord rotaItinerario = new ItinerarioRecord(rota.getId(), paradasItinerario);
            return ResponseEntity.ok(new GenericResponse<>("200", "Itinerario criado.", rotaItinerario));

        } catch (Exception e) {
            logger.error("Erro ao criar itinerario", e);
            GenericResponse<ItinerarioRecord> errorResponse = new GenericResponse<>("500", "Erro ao criar itinerario: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<ParadaRecord> createParadasForItinerario(List<ParadaRecord> paradasACriar, Map<Integer, ParadaRecord> indexedParadas) {
        if (paradasACriar.isEmpty()) {
            return new ArrayList<>(indexedParadas.values());
        }

        ResponseEntity<GenericResponse<List<GenericResponse<ParadaRecord>>>> responseCreateParadas =
                paradaService.createParadas(paradasACriar);

        GenericResponse<List<GenericResponse<ParadaRecord>>> body = responseCreateParadas.getBody();
        if (body == null || body.data() == null) {
            throw new RuntimeException("Error: Response from createParadas is null.");
        }

        List<ParadaRecord> createdParadas = new ArrayList<>();
        List<GenericResponse<ParadaRecord>> failedParadas = new ArrayList<>();

        for (GenericResponse<ParadaRecord> recordResponse : body.data()) {
            if ("200".equals(recordResponse.status())) {
                createdParadas.add(recordResponse.data());
            } else {
                failedParadas.add(recordResponse);
            }
        }

        if (!failedParadas.isEmpty()) {
            throw new RuntimeException("Some paradas failed to be created: " + failedParadas);
        }

        Iterator<ParadaRecord> createdIterator = createdParadas.iterator();
        indexedParadas.replaceAll((pos, parada) -> parada == null ? createdIterator.next() : parada);
        return new ArrayList<>(indexedParadas.values());
    }
}
