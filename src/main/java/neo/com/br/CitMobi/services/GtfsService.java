package neo.com.br.CitMobi.services;

import com.google.transit.realtime.GtfsRealtime.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.telemetria.Telemetria;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.telemetria.TelemetriaRepository;
import neo.com.br.CitMobi.repository.viagem.ViagemRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
@Slf4j
public class GtfsService {

    private final TelemetriaRepository telemetriaRepository;
    private final ViagemRepository viagemRepository;
    private final OperadorRepository operadorRepository;
    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;
    private final ParadaRepository paradaRepository;

    /**
     * Gera o Feed Protobuf binário do GTFS-Realtime VehiclePositions.
     */
    public byte[] generateVehiclePositionsProtobuf() {
        FeedMessage feedMessage = buildVehiclePositionsFeedMessage();
        return feedMessage.toByteArray();
    }

    /**
     * Retorna a estrutura do Feed GTFS-Realtime VehiclePositions em formato estruturado (Map/JSON) para inspeção.
     */
    public Map<String, Object> generateVehiclePositionsJson() {
        FeedMessage feedMessage = buildVehiclePositionsFeedMessage();
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("gtfsRealtimeVersion", feedMessage.getHeader().getGtfsRealtimeVersion());
        header.put("incrementality", feedMessage.getHeader().getIncrementality().name());
        header.put("timestamp", feedMessage.getHeader().getTimestamp());
        response.put("header", header);

        List<Map<String, Object>> entities = new ArrayList<>();
        for (FeedEntity entity : feedMessage.getEntityList()) {
            Map<String, Object> eMap = new LinkedHashMap<>();
            eMap.put("id", entity.getId());

            if (entity.hasVehicle()) {
                VehiclePosition vp = entity.getVehicle();
                Map<String, Object> vpMap = new LinkedHashMap<>();

                if (vp.hasVehicle()) {
                    Map<String, Object> vDesc = new LinkedHashMap<>();
                    vDesc.put("id", vp.getVehicle().getId());
                    vDesc.put("label", vp.getVehicle().getLabel());
                    vDesc.put("licensePlate", vp.getVehicle().getLicensePlate());
                    vpMap.put("vehicle", vDesc);
                }

                if (vp.hasTrip()) {
                    Map<String, Object> tDesc = new LinkedHashMap<>();
                    tDesc.put("tripId", vp.getTrip().getTripId());
                    tDesc.put("routeId", vp.getTrip().getRouteId());
                    vpMap.put("trip", tDesc);
                }

                if (vp.hasPosition()) {
                    Map<String, Object> pos = new LinkedHashMap<>();
                    pos.put("latitude", vp.getPosition().getLatitude());
                    pos.put("longitude", vp.getPosition().getLongitude());
                    pos.put("bearing", vp.getPosition().getBearing());
                    pos.put("speed", vp.getPosition().getSpeed());
                    pos.put("odometer", vp.getPosition().getOdometer());
                    vpMap.put("position", pos);
                }

                vpMap.put("currentStopSequence", vp.getCurrentStopSequence());
                vpMap.put("currentStatus", vp.getCurrentStatus().name());
                vpMap.put("stopId", vp.getStopId());
                vpMap.put("timestamp", vp.getTimestamp());

                eMap.put("vehicle", vpMap);
            }
            entities.add(eMap);
        }

        response.put("entity", entities);
        return response;
    }

    private FeedMessage buildVehiclePositionsFeedMessage() {
        FeedHeader.Builder headerBuilder = FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(FeedHeader.Incrementality.FULL_DATASET)
                .setTimestamp(Instant.now().getEpochSecond());

        FeedMessage.Builder feedMessageBuilder = FeedMessage.newBuilder()
                .setHeader(headerBuilder);

        List<Telemetria> telemetrias = telemetriaRepository.findByViagem_Status(ViagemStatus.EM_ANDAMENTO);

        for (Telemetria t : telemetrias) {
            Veiculo veiculo = t.getVeiculo();
            Viagem viagem = t.getViagem();
            if (veiculo == null || viagem == null) continue;

            String entityId = "VP_" + veiculo.getId();
            FeedEntity.Builder entityBuilder = FeedEntity.newBuilder().setId(entityId);

            VehiclePosition.Builder vpBuilder = VehiclePosition.newBuilder();

            // 1. Vehicle Descriptor
            VehicleDescriptor.Builder vDesc = VehicleDescriptor.newBuilder()
                    .setId(String.valueOf(veiculo.getId()));
            if (veiculo.getPlaca() != null) vDesc.setLicensePlate(veiculo.getPlaca());
            if (veiculo.getCodigoVeiculo() != null) vDesc.setLabel(veiculo.getCodigoVeiculo());
            vpBuilder.setVehicle(vDesc);

            // 2. Trip Descriptor
            TripDescriptor.Builder tDesc = TripDescriptor.newBuilder()
                    .setTripId(String.valueOf(viagem.getId()));
            if (viagem.getLinha() != null) {
                tDesc.setRouteId(String.valueOf(viagem.getLinha().getId()));
            }
            vpBuilder.setTrip(tDesc);

            // 3. Geographic Position
            Position.Builder posBuilder = Position.newBuilder();
            if (t.getLatitude() != null) posBuilder.setLatitude(t.getLatitude().floatValue());
            if (t.getLongitude() != null) posBuilder.setLongitude(t.getLongitude().floatValue());
            if (t.getBearing() != null) posBuilder.setBearing(t.getBearing().floatValue());
            if (t.getVelocidade() != null) posBuilder.setSpeed(t.getVelocidade().floatValue());
            if (t.getOdometer() != null) posBuilder.setOdometer(t.getOdometer().doubleValue());
            vpBuilder.setPosition(posBuilder);

            // 4. Stop Context
            if (t.getSequenciaParadaAtual() != null) {
                vpBuilder.setCurrentStopSequence(t.getSequenciaParadaAtual());
            }
            if (t.getParadaAtual() != null) {
                vpBuilder.setStopId(String.valueOf(t.getParadaAtual().getId()));
            }

            // Stop Status
            if (t.getStatusParadaAtual() != null) {
                try {
                    vpBuilder.setCurrentStatus(VehiclePosition.VehicleStopStatus.valueOf(t.getStatusParadaAtual().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    vpBuilder.setCurrentStatus(VehiclePosition.VehicleStopStatus.IN_TRANSIT_TO);
                }
            } else {
                vpBuilder.setCurrentStatus(VehiclePosition.VehicleStopStatus.IN_TRANSIT_TO);
            }

            // Timestamp
            if (t.getUltimaAtualizacao() != null) {
                vpBuilder.setTimestamp(t.getUltimaAtualizacao().toEpochSecond());
            } else {
                vpBuilder.setTimestamp(Instant.now().getEpochSecond());
            }

            entityBuilder.setVehicle(vpBuilder);
            feedMessageBuilder.addEntity(entityBuilder);
        }

        return feedMessageBuilder.build();
    }

    /**
     * Gera o Feed Protobuf binário do GTFS-Realtime TripUpdates.
     */
    public byte[] generateTripUpdatesProtobuf() {
        FeedMessage feedMessage = buildTripUpdatesFeedMessage();
        return feedMessage.toByteArray();
    }

    public Map<String, Object> generateTripUpdatesJson() {
        FeedMessage feedMessage = buildTripUpdatesFeedMessage();
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("gtfsRealtimeVersion", feedMessage.getHeader().getGtfsRealtimeVersion());
        header.put("incrementality", feedMessage.getHeader().getIncrementality().name());
        header.put("timestamp", feedMessage.getHeader().getTimestamp());
        response.put("header", header);

        List<Map<String, Object>> entities = new ArrayList<>();
        for (FeedEntity entity : feedMessage.getEntityList()) {
            Map<String, Object> eMap = new LinkedHashMap<>();
            eMap.put("id", entity.getId());

            if (entity.hasTripUpdate()) {
                TripUpdate tu = entity.getTripUpdate();
                Map<String, Object> tuMap = new LinkedHashMap<>();

                if (tu.hasTrip()) {
                    Map<String, Object> tDesc = new LinkedHashMap<>();
                    tDesc.put("tripId", tu.getTrip().getTripId());
                    tDesc.put("routeId", tu.getTrip().getRouteId());
                    tuMap.put("trip", tDesc);
                }

                if (tu.hasVehicle()) {
                    Map<String, Object> vDesc = new LinkedHashMap<>();
                    vDesc.put("id", tu.getVehicle().getId());
                    vDesc.put("licensePlate", tu.getVehicle().getLicensePlate());
                    tuMap.put("vehicle", vDesc);
                }

                tuMap.put("timestamp", tu.getTimestamp());
                eMap.put("tripUpdate", tuMap);
            }
            entities.add(eMap);
        }

        response.put("entity", entities);
        return response;
    }

    private FeedMessage buildTripUpdatesFeedMessage() {
        FeedHeader.Builder headerBuilder = FeedHeader.newBuilder()
                .setGtfsRealtimeVersion("2.0")
                .setIncrementality(FeedHeader.Incrementality.FULL_DATASET)
                .setTimestamp(Instant.now().getEpochSecond());

        FeedMessage.Builder feedMessageBuilder = FeedMessage.newBuilder()
                .setHeader(headerBuilder);

        List<Viagem> viagensAtivas = viagemRepository.findByStatus(ViagemStatus.EM_ANDAMENTO);

        for (Viagem v : viagensAtivas) {
            String entityId = "TU_" + v.getId();
            FeedEntity.Builder entityBuilder = FeedEntity.newBuilder().setId(entityId);

            TripUpdate.Builder tuBuilder = TripUpdate.newBuilder();

            TripDescriptor.Builder tDesc = TripDescriptor.newBuilder()
                    .setTripId(String.valueOf(v.getId()));
            if (v.getLinha() != null) {
                tDesc.setRouteId(String.valueOf(v.getLinha().getId()));
            }
            tuBuilder.setTrip(tDesc);

            if (v.getVeiculo() != null) {
                VehicleDescriptor.Builder vDesc = VehicleDescriptor.newBuilder()
                        .setId(String.valueOf(v.getVeiculo().getId()));
                if (v.getVeiculo().getPlaca() != null) vDesc.setLicensePlate(v.getVeiculo().getPlaca());
                tuBuilder.setVehicle(vDesc);
            }

            tuBuilder.setTimestamp(Instant.now().getEpochSecond());

            entityBuilder.setTripUpdate(tuBuilder);
            feedMessageBuilder.addEntity(entityBuilder);
        }

        return feedMessageBuilder.build();
    }

    /**
     * Gera o arquivo ZIP contendo os feeds estáticos padrão GTFS:
     * agency.txt, routes.txt, trips.txt, stops.txt
     */
    public byte[] generateStaticGtfsZip() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {

            // 1. agency.txt
            zos.putNextEntry(new ZipEntry("agency.txt"));
            StringBuilder agencyCsv = new StringBuilder("agency_id,agency_name,agency_url,agency_timezone,agency_lang\n");
            List<Operador> operadores = operadorRepository.findAll();
            for (Operador op : operadores) {
                agencyCsv.append(op.getId()).append(",")
                        .append(escapeCsv(op.getRazaoSocial())).append(",")
                        .append("https://citmobi.com.br,")
                        .append("America/Sao_Paulo,")
                        .append("pt\n");
            }
            zos.write(agencyCsv.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // 2. routes.txt
            zos.putNextEntry(new ZipEntry("routes.txt"));
            StringBuilder routesCsv = new StringBuilder("route_id,agency_id,route_short_name,route_long_name,route_type\n");
            List<Linha> linhas = linhaRepository.findAll();
            for (Linha l : linhas) {
                Long agencyId = l.getOperador() != null ? l.getOperador().getId() : 1L;
                routesCsv.append(l.getId()).append(",")
                        .append(agencyId).append(",")
                        .append(escapeCsv(l.getCodigoLinha())).append(",")
                        .append(escapeCsv(l.getDescricao() != null ? l.getDescricao() : l.getCodigoLinha())).append(",")
                        .append("3\n"); // 3 = Bus in GTFS spec
            }
            zos.write(routesCsv.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // 3. stops.txt
            zos.putNextEntry(new ZipEntry("stops.txt"));
            StringBuilder stopsCsv = new StringBuilder("stop_id,stop_name,stop_lat,stop_lon,location_type\n");
            List<Parada> paradas = paradaRepository.findAll();
            for (Parada p : paradas) {
                stopsCsv.append(p.getId()).append(",")
                        .append(escapeCsv(p.getLogradouro() != null ? p.getLogradouro() : "Parada " + p.getId())).append(",")
                        .append(p.getLatitude() != null ? p.getLatitude() : "0.0").append(",")
                        .append(p.getLongitude() != null ? p.getLongitude() : "0.0").append(",")
                        .append("0\n"); // 0 = Stop/Platform
            }
            zos.write(stopsCsv.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // 4. trips.txt
            zos.putNextEntry(new ZipEntry("trips.txt"));
            StringBuilder tripsCsv = new StringBuilder("route_id,service_id,trip_id,trip_headsign,direction_id\n");
            List<Rota> rotas = rotaRepository.findAll();
            for (Rota r : rotas) {
                if (r.getLinha() == null) continue;
                int directionId = "VOLTA".equalsIgnoreCase(r.getSentido()) ? 1 : 0;
                tripsCsv.append(r.getLinha().getId()).append(",")
                        .append("REGULAR,")
                        .append(r.getId()).append(",")
                        .append(escapeCsv(r.getPrefixo() != null ? r.getPrefixo() : r.getSentido())).append(",")
                        .append(directionId).append("\n");
            }
            zos.write(tripsCsv.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }

        return baos.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
