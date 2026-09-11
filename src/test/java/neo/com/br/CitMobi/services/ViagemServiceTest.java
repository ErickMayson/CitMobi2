package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.viagem.ViagemRecord;
import neo.com.br.CitMobi.models.records.viagem.ViagemRequest;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoStatus;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.veiculo.VeiculoRepository;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private LinhaRepository linhaRepository;
    @Mock
    private RotaRepository rotaRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ViagemService viagemService;

    private Usuario motorista;
    private Veiculo veiculo;
    private Linha linha;
    private Rota rota;
    private Operador operador;

    @BeforeEach
    void setUp() {
        operador = new Operador(1L, "01234567890123", "Cit Mobi", "S");

        motorista = new Usuario();
        motorista.setId(UUID.randomUUID());
        motorista.setLogin("motorista1");
        motorista.setNome("Carlos Silva");
        motorista.setRole(UsuarioRole.MOTORISTA);
        motorista.setFlagAtivo("S");
        motorista.setOperador(operador);

        veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setPlaca("ABC1234");
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
    }

    @Test
    @DisplayName("Should successfully start an operational trip session")
    void shouldStartViagemSuccessfully() {
        ViagemRequest req = new ViagemRequest(motorista.getId(), 10L, 100L, 1000L);

        when(usuarioRepository.findById(motorista.getId())).thenReturn(Optional.of(motorista));
        when(viagemRepository.findFirstByMotorista_IdAndStatus(motorista.getId(), ViagemStatus.EM_ANDAMENTO)).thenReturn(Optional.empty());
        when(veiculoRepository.findById(10L)).thenReturn(Optional.of(veiculo));
        when(viagemRepository.findFirstByVeiculo_IdAndStatus(10L, ViagemStatus.EM_ANDAMENTO)).thenReturn(Optional.empty());
        when(linhaRepository.findById(100L)).thenReturn(Optional.of(linha));
        when(rotaRepository.findById(1000L)).thenReturn(Optional.of(rota));

        Viagem savedViagem = new Viagem(motorista, veiculo, linha, rota);
        savedViagem.setId(500L);
        savedViagem.setDataInicio(OffsetDateTime.now());
        savedViagem.setStatus(ViagemStatus.EM_ANDAMENTO);
        when(viagemRepository.save(any(Viagem.class))).thenReturn(savedViagem);

        ResponseEntity<GenericResponse<ViagemRecord>> response = viagemService.iniciarViagem(req, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("201", response.getBody().status());
        assertEquals(500L, response.getBody().data().id());
        assertEquals("EM_ANDAMENTO", response.getBody().data().status());
    }

    @Test
    @DisplayName("Should reject starting a trip when driver already has an active trip")
    void shouldRejectStartTripWhenDriverAlreadyActive() {
        ViagemRequest req = new ViagemRequest(motorista.getId(), 10L, 100L, 1000L);

        when(usuarioRepository.findById(motorista.getId())).thenReturn(Optional.of(motorista));
        Viagem existing = new Viagem(motorista, veiculo, linha, rota);
        existing.setId(99L);
        when(viagemRepository.findFirstByMotorista_IdAndStatus(motorista.getId(), ViagemStatus.EM_ANDAMENTO)).thenReturn(Optional.of(existing));

        ResponseEntity<GenericResponse<ViagemRecord>> response = viagemService.iniciarViagem(req, null);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Motorista já possui uma viagem"));
    }

    @Test
    @DisplayName("Should reject starting a trip when vehicle is in maintenance")
    void shouldRejectStartTripWhenVehicleInMaintenance() {
        veiculo.setStatus(VeiculoStatus.MANUTENCAO);
        ViagemRequest req = new ViagemRequest(motorista.getId(), 10L, 100L, 1000L);

        when(usuarioRepository.findById(motorista.getId())).thenReturn(Optional.of(motorista));
        when(viagemRepository.findFirstByMotorista_IdAndStatus(motorista.getId(), ViagemStatus.EM_ANDAMENTO)).thenReturn(Optional.empty());
        when(veiculoRepository.findById(10L)).thenReturn(Optional.of(veiculo));

        ResponseEntity<GenericResponse<ViagemRecord>> response = viagemService.iniciarViagem(req, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("não está apto para operação"));
    }

    @Test
    @DisplayName("Should successfully finalize an active trip")
    void shouldFinalizeActiveTripSuccessfully() {
        Viagem trip = new Viagem(motorista, veiculo, linha, rota);
        trip.setId(500L);
        trip.setStatus(ViagemStatus.EM_ANDAMENTO);
        trip.setDataInicio(OffsetDateTime.now().minusHours(1));

        when(viagemRepository.findById(500L)).thenReturn(Optional.of(trip));
        when(viagemRepository.save(any(Viagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<GenericResponse<ViagemRecord>> response = viagemService.finalizarViagem(500L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("FINALIZADA", response.getBody().data().status());
        assertNotNull(response.getBody().data().dataFim());
    }

    @Test
    @DisplayName("Should retrieve active trips with operator and regulator scoping")
    void shouldGetViagensAtivas() {
        Viagem trip = new Viagem(motorista, veiculo, linha, rota);
        trip.setId(500L);
        trip.setStatus(ViagemStatus.EM_ANDAMENTO);

        when(viagemRepository.findByStatus(ViagemStatus.EM_ANDAMENTO)).thenReturn(List.of(trip));
        when(tokenService.getRoleFromToken(any())).thenReturn("ROLE_ADMIN");

        ResponseEntity<GenericResponse<List<ViagemRecord>>> response = viagemService.getViagensAtivas(null, null, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().data().size());
        assertEquals("ABC1234", response.getBody().data().get(0).veiculoPlaca());
    }
}
