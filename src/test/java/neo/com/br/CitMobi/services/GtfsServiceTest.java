package neo.com.br.CitMobi.services;

import com.google.transit.realtime.GtfsRealtime.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtfsServiceTest {

    @Mock
    private TelemetriaRepository telemetriaRepository;
    @Mock
    private ViagemRepository viagemRepository;
    @Mock
    private OperadorRepository operadorRepository;
    @Mock
    private LinhaRepository linhaRepository;
    @Mock
    private RotaRepository rotaRepository;
    @Mock
    private ParadaRepository paradaRepository;

    @InjectMocks
    private GtfsService gtfsService;

    private Operador operador;
    private Linha linha;
    private Rota rota;
    private Veiculo veiculo;
    private Viagem viagem;
    private Parada parada;
    private Telemetria telemetria;

    @BeforeEach
    void setUp() {
        operador = new Operador(1L, "01234567890123", "Cit Mobi Operadora", "S");

        linha = new Linha();
        linha.setId(100L);
        linha.setCodigoLinha("3301");
        linha.setLinhaDescricao("Term. Central / Bairro");
        linha.setOperador(operador);

        rota = new Rota();
        rota.setId(1000L);
        rota.setPrefixo("3301-10");
        rota.setSentido("IDA");
        rota.setLinha(linha);

        veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setPlaca("BRA4E56");
        veiculo.setCodigoVeiculo("BUS-10");
        veiculo.setOperador(operador);

        viagem = new Viagem(null, veiculo, linha, rota);
        viagem.setId(500L);
        viagem.setStatus(ViagemStatus.EM_ANDAMENTO);

        parada = new Parada();
        parada.setParadaId(1L);
        parada.setLogradouro("Av. Paulista, 1000");
        parada.setLatitude(new BigDecimal("-23.561414"));
        parada.setLongitude(new BigDecimal("-46.655881"));

        telemetria = new Telemetria();
        telemetria.setVeiculoId(10L);
        telemetria.setVeiculo(veiculo);
        telemetria.setViagem(viagem);
        telemetria.setLatitude(new BigDecimal("-23.550520"));
        telemetria.setLongitude(new BigDecimal("-46.633308"));
        telemetria.setVelocidade(new BigDecimal("12.5"));
        telemetria.setBearing(new BigDecimal("180"));
        telemetria.setOdometer(new BigDecimal("5000"));
        telemetria.setSequenciaParadaAtual(2);
        telemetria.setStatusParadaAtual("IN_TRANSIT_TO");
        telemetria.setParadaAtual(parada);
        telemetria.setUltimaAtualizacao(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Should generate valid binary Protobuf FeedMessage for GTFS-RT VehiclePositions")
    void shouldGenerateVehiclePositionsProtobuf() throws Exception {
        when(telemetriaRepository.findByViagem_Status(ViagemStatus.EM_ANDAMENTO)).thenReturn(List.of(telemetria));

        byte[] protobufBytes = gtfsService.generateVehiclePositionsProtobuf();
        assertNotNull(protobufBytes);
        assertTrue(protobufBytes.length > 0);

        FeedMessage parsed = FeedMessage.parseFrom(protobufBytes);
        assertEquals("2.0", parsed.getHeader().getGtfsRealtimeVersion());
        assertEquals(1, parsed.getEntityCount());

        FeedEntity entity = parsed.getEntity(0);
        assertEquals("VP_10", entity.getId());
        assertTrue(entity.hasVehicle());

        VehiclePosition vp = entity.getVehicle();
        assertEquals("10", vp.getVehicle().getId());
        assertEquals("BRA4E56", vp.getVehicle().getLicensePlate());
        assertEquals("500", vp.getTrip().getTripId());
        assertEquals("100", vp.getTrip().getRouteId());
        assertEquals(-23.550520f, vp.getPosition().getLatitude(), 0.0001);
        assertEquals(-46.633308f, vp.getPosition().getLongitude(), 0.0001);
        assertEquals(12.5f, vp.getPosition().getSpeed(), 0.01);
        assertEquals(VehiclePosition.VehicleStopStatus.IN_TRANSIT_TO, vp.getCurrentStatus());
    }

    @Test
    @DisplayName("Should generate valid JSON inspection map for GTFS-RT VehiclePositions")
    void shouldGenerateVehiclePositionsJson() {
        when(telemetriaRepository.findByViagem_Status(ViagemStatus.EM_ANDAMENTO)).thenReturn(List.of(telemetria));

        Map<String, Object> json = gtfsService.generateVehiclePositionsJson();
        assertNotNull(json);
        assertTrue(json.containsKey("header"));
        assertTrue(json.containsKey("entity"));

        List<?> entities = (List<?>) json.get("entity");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Should generate valid binary Protobuf FeedMessage for GTFS-RT TripUpdates")
    void shouldGenerateTripUpdatesProtobuf() throws Exception {
        when(viagemRepository.findByStatus(ViagemStatus.EM_ANDAMENTO)).thenReturn(List.of(viagem));

        byte[] protobufBytes = gtfsService.generateTripUpdatesProtobuf();
        assertNotNull(protobufBytes);
        assertTrue(protobufBytes.length > 0);

        FeedMessage parsed = FeedMessage.parseFrom(protobufBytes);
        assertEquals(1, parsed.getEntityCount());
        assertEquals("TU_500", parsed.getEntity(0).getId());
    }

    @Test
    @DisplayName("Should generate standard Static GTFS Zip containing agency, routes, stops, trips")
    void shouldGenerateStaticGtfsZip() throws IOException {
        when(operadorRepository.findAll()).thenReturn(List.of(operador));
        when(linhaRepository.findAll()).thenReturn(List.of(linha));
        when(paradaRepository.findAll()).thenReturn(List.of(parada));
        when(rotaRepository.findAll()).thenReturn(List.of(rota));

        byte[] zipBytes = gtfsService.generateStaticGtfsZip();
        assertNotNull(zipBytes);
        assertTrue(zipBytes.length > 0);

        // Verify ZIP entries
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            int count = 0;
            while ((entry = zis.getNextEntry()) != null) {
                count++;
                assertTrue(entry.getName().endsWith(".txt"));
            }
            assertEquals(4, count); // agency.txt, routes.txt, stops.txt, trips.txt
        }
    }
}
