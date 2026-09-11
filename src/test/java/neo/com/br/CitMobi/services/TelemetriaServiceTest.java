package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaHistoricoRecord;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaPingRequest;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaVeiculoRecord;
import neo.com.br.CitMobi.models.telemetria.Telemetria;
import neo.com.br.CitMobi.models.telemetria.TelemetriaHistorico;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoStatus;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.telemetria.TelemetriaHistoricoRepository;
import neo.com.br.CitMobi.repository.telemetria.TelemetriaRepository;
import neo.com.br.CitMobi.repository.viagem.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetriaServiceTest {

    @Mock
    private TelemetriaRepository telemetriaRepository;
    @Mock
    private TelemetriaHistoricoRepository telemetriaHistoricoRepository;
    @Mock
    private ViagemRepository viagemRepository;
    @Mock
    private ParadaRepository paradaRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TelemetriaService telemetriaService;

    private Viagem viagem;
    private Veiculo veiculo;
    private Linha linha;
    private Rota rota;
    private Operador operador;
    private Usuario motorista;

    @BeforeEach
    void setUp() {
        operador = new Operador(1L, "01234567890123", "Cit Mobi", "S");

        motorista = new Usuario();
        motorista.setId(UUID.randomUUID());
        motorista.setNome("Antonio Silva");

        veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setPlaca("BRA4E56");
        veiculo.setCodigoVeiculo("BUS-10");
        veiculo.setStatus(VeiculoStatus.ATIVO);
        veiculo.setOperador(operador);

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

        viagem = new Viagem(motorista, veiculo, linha, rota);
        viagem.setId(500L);
        viagem.setStatus(ViagemStatus.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("Should successfully ingest GPS ping and update real-time cache and history")
    void shouldIngestGpsPingSuccessfully() {
        TelemetriaPingRequest req = new TelemetriaPingRequest(
                500L,
                new BigDecimal("-23.550520"),
                new BigDecimal("-46.633308"),
                new BigDecimal("42.50"),
                new BigDecimal("180.00"),
                new BigDecimal("1250.00"),
                3,
                "IN_TRANSIT_TO",
                null
        );

        when(viagemRepository.findById(500L)).thenReturn(Optional.of(viagem));
        when(telemetriaRepository.findByVeiculoId(10L)).thenReturn(Optional.empty());
        when(telemetriaRepository.save(any(Telemetria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> response = telemetriaService.registrarPing(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(new BigDecimal("-23.550520"), response.getBody().data().latitude());
        assertEquals(new BigDecimal("42.50"), response.getBody().data().velocidade());
        assertEquals(3, response.getBody().data().sequenciaParadaAtual());

        verify(telemetriaRepository, times(1)).save(any(Telemetria.class));
        verify(telemetriaHistoricoRepository, times(1)).save(any(TelemetriaHistorico.class));
    }

    @Test
    @DisplayName("Should reject GPS ping when trip is not in progress")
    void shouldRejectPingWhenTripNotInProgress() {
        viagem.setStatus(ViagemStatus.FINALIZADA);
        TelemetriaPingRequest req = new TelemetriaPingRequest(
                500L,
                new BigDecimal("-23.550520"),
                new BigDecimal("-46.633308"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                null,
                null
        );

        when(viagemRepository.findById(500L)).thenReturn(Optional.of(viagem));

        ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> response = telemetriaService.registrarPing(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("não está em andamento"));
        verify(telemetriaRepository, never()).save(any());
        verify(telemetriaHistoricoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should query active vehicle positions")
    void shouldGetVeiculosAtivos() {
        Telemetria t = new Telemetria();
        t.setVeiculoId(10L);
        t.setVeiculo(veiculo);
        t.setViagem(viagem);
        t.setLatitude(new BigDecimal("-23.550520"));
        t.setLongitude(new BigDecimal("-46.633308"));
        t.setVelocidade(new BigDecimal("35.00"));
        t.setBearing(new BigDecimal("90.00"));
        t.setUltimaAtualizacao(OffsetDateTime.now());

        when(telemetriaRepository.findByViagem_Status(ViagemStatus.EM_ANDAMENTO)).thenReturn(List.of(t));
        when(tokenService.getRoleFromToken(any())).thenReturn("ROLE_ADMIN");

        ResponseEntity<GenericResponse<List<TelemetriaVeiculoRecord>>> response = telemetriaService.getVeiculosAtivos(null, null, null, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().data().size());
        assertEquals("BRA4E56", response.getBody().data().get(0).placa());
    }

    @Test
    @DisplayName("Should retrieve historical trajectory breadcrumbs for a trip")
    void shouldGetHistoricoViagem() {
        TelemetriaHistorico h1 = new TelemetriaHistorico(1L, viagem, new BigDecimal("-23.5501"), new BigDecimal("-46.6331"), new BigDecimal("30"), new BigDecimal("0"), new BigDecimal("100"), OffsetDateTime.now().minusMinutes(5));
        TelemetriaHistorico h2 = new TelemetriaHistorico(2L, viagem, new BigDecimal("-23.5505"), new BigDecimal("-46.6335"), new BigDecimal("40"), new BigDecimal("45"), new BigDecimal("250"), OffsetDateTime.now());

        when(telemetriaHistoricoRepository.findByViagem_IdOrderByDataRegistroAsc(500L)).thenReturn(List.of(h1, h2));

        ResponseEntity<GenericResponse<List<TelemetriaHistoricoRecord>>> response = telemetriaService.getHistoricoViagem(500L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().data().size());
        assertEquals(new BigDecimal("-23.5501"), response.getBody().data().get(0).latitude());
        assertEquals(new BigDecimal("-23.5505"), response.getBody().data().get(1).latitude());
    }
}
