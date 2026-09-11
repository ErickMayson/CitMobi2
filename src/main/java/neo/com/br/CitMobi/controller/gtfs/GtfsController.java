package neo.com.br.CitMobi.controller.gtfs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.services.GtfsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "GTFS & GTFS-Realtime", description = "Endpoints serving standard GTFS Static and GTFS-Realtime feeds")
@AllArgsConstructor
public class GtfsController {

    private final GtfsService gtfsService;

    @GetMapping(value = "/gtfs-rt/vehicle-positions", produces = {"application/x-protobuf", MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Retorna o feed binário GTFS-Realtime VehiclePositions (Protobuf FeedMessage)")
    public ResponseEntity<byte[]> getVehiclePositionsProtobuf() {
        byte[] payload = gtfsService.generateVehiclePositionsProtobuf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"vehicle-positions.pb\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(payload);
    }

    @GetMapping(value = "/gtfs-rt/vehicle-positions/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retorna a representação legível em JSON do feed GTFS-Realtime VehiclePositions para depuração")
    public ResponseEntity<?> getVehiclePositionsJson() {
        return ResponseEntity.ok(gtfsService.generateVehiclePositionsJson());
    }

    @GetMapping(value = "/gtfs-rt/trip-updates", produces = {"application/x-protobuf", MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Retorna o feed binário GTFS-Realtime TripUpdates (Protobuf FeedMessage)")
    public ResponseEntity<byte[]> getTripUpdatesProtobuf() {
        byte[] payload = gtfsService.generateTripUpdatesProtobuf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"trip-updates.pb\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(payload);
    }

    @GetMapping(value = "/gtfs-rt/trip-updates/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retorna a representação legível em JSON do feed GTFS-Realtime TripUpdates para depuração")
    public ResponseEntity<?> getTripUpdatesJson() {
        return ResponseEntity.ok(gtfsService.generateTripUpdatesJson());
    }

    @GetMapping(value = "/gtfs/feed.zip", produces = "application/zip")
    @Operation(summary = "Exporta o arquivo ZIP padrão GTFS Static (agency.txt, routes.txt, trips.txt, stops.txt)")
    public ResponseEntity<byte[]> getStaticGtfsZip() throws IOException {
        byte[] zipBytes = gtfsService.generateStaticGtfsZip();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gtfs-feed.zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zipBytes);
    }
}
