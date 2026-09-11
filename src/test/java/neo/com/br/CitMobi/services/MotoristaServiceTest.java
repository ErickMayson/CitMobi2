package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.motorista.MotoristaHorarioRecord;
import neo.com.br.CitMobi.models.records.motorista.MotoristaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.models.veiculo.MotoristaHora;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoEscala;
import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.usuario.MotoristaHoraRepository;
import neo.com.br.CitMobi.repository.veiculo.VeiculoEscalaRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MotoristaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VeiculoEscalaRepository veiculoEscalaRepository;

    @Mock
    private MotoristaHoraRepository motoristaHoraRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private LinhaRepository linhaRepository;

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private MotoristaService motoristaService;

    private Operador sampleOperador1;
    private Operador sampleOperador2;
    private Usuario sampleDriver;
    private Veiculo sampleVeiculo1;
    private Veiculo sampleVeiculo2;
    private Linha sampleLinha;

    @BeforeEach
    void setUp() {
        sampleOperador1 = new Operador("01234567890123", "Cit Mobi", "S");
        sampleOperador1.setId(1L);

        sampleOperador2 = new Operador("98765432000199", "Viacao Gato Preto", "N");
        sampleOperador2.setId(2L);

        sampleDriver = new Usuario();
        sampleDriver.setId(UUID.fromString("00000000-0000-0000-0003-000000000001"));
        sampleDriver.setNome("Carlos Silva");
        sampleDriver.setLogin("12345678900");
        sampleDriver.setCpf("12345678900");
        sampleDriver.setCnhNumero("98765432100");
        sampleDriver.setCnhValidade(LocalDate.now().plusYears(2));
        sampleDriver.setRole(UsuarioRole.MOTORISTA);
        sampleDriver.setFlagAtivo("S");
        sampleDriver.setOperador(sampleOperador1);

        sampleVeiculo1 = new Veiculo();
        sampleVeiculo1.setId(1L);
        sampleVeiculo1.setPlaca("ABC1D23");
        sampleVeiculo1.setCodigoVeiculo("BUS-01");
        sampleVeiculo1.setOperador(sampleOperador1);

        sampleVeiculo2 = new Veiculo();
        sampleVeiculo2.setId(2L);
        sampleVeiculo2.setPlaca("XYZ9A87");
        sampleVeiculo2.setCodigoVeiculo("BUS-02");
        sampleVeiculo2.setOperador(sampleOperador1);

        sampleLinha = new Linha();
        sampleLinha.setId(10L);
        sampleLinha.setCodigoLinha("3301");
        sampleLinha.setAtendimento("10");
    }

    @Test
    @DisplayName("Should retrieve all motoristas mapped to MotoristaRecord")
    void shouldGetAllMotoristasSuccessfully() {
        when(usuarioRepository.findByOperador_IdAndRoleAndFlagAtivo(1L, UsuarioRole.MOTORISTA, "S"))
                .thenReturn(List.of(sampleDriver));
        when(veiculoEscalaRepository.findByMotorista_Id(sampleDriver.getId())).thenReturn(Collections.emptyList());
        when(motoristaHoraRepository.findByMotorista_Id(sampleDriver.getId())).thenReturn(Collections.emptyList());
        when(viagemRepository.findFirstByMotorista_IdAndStatus(sampleDriver.getId(), ViagemStatus.EM_ANDAMENTO))
                .thenReturn(Optional.empty());

        ResponseEntity<GenericResponse<List<MotoristaRecord>>> response = motoristaService.getAllMotoristas(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(1, response.getBody().data().size());
        assertEquals("Carlos Silva", response.getBody().data().get(0).nome());
        assertEquals("98765432100", response.getBody().data().get(0).cnhNumero());
        assertEquals("FORA DE TURNO", response.getBody().data().get(0).status());
    }

    @Test
    @DisplayName("Should create driver with valid CNH and future expiration date")
    void shouldCreateMotoristaWithValidCnh() {
        MotoristaRecord record = new MotoristaRecord(
                null,
                "Pedro Alvares",
                "11122233344",
                "CNH-1234567",
                LocalDate.now().plusYears(3),
                "pedro",
                "11988887777",
                1L,
                "Cit Mobi",
                "FORA DE TURNO",
                Collections.emptyList()
        );

        when(operadorRepository.findById(1L)).thenReturn(Optional.of(sampleOperador1));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.createMotorista(record, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("201", response.getBody().status());
        assertEquals("Pedro Alvares", response.getBody().data().nome());
        assertEquals("CNH-1234567", response.getBody().data().cnhNumero());
    }

    @Test
    @DisplayName("Should reject driver creation when CNH number is missing")
    void shouldRejectMotoristaCreationWithoutCnh() {
        MotoristaRecord record = new MotoristaRecord(
                null,
                "Pedro Alvares",
                "11122233344",
                "",
                LocalDate.now().plusYears(3),
                "pedro",
                "11988887777",
                1L,
                "Cit Mobi",
                "FORA DE TURNO",
                Collections.emptyList()
        );

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.createMotorista(record, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().status());
        assertTrue(response.getBody().message().contains("CNH number is required"));
    }

    @Test
    @DisplayName("Should reject driver creation when CNH validity date is expired")
    void shouldRejectMotoristaCreationWithExpiredCnh() {
        MotoristaRecord record = new MotoristaRecord(
                null,
                "Pedro Alvares",
                "11122233344",
                "CNH-1234567",
                LocalDate.now().minusDays(1),
                "pedro",
                "11988887777",
                1L,
                "Cit Mobi",
                "FORA DE TURNO",
                Collections.emptyList()
        );

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.createMotorista(record, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().status());
        assertTrue(response.getBody().message().contains("expired"));
    }

    @Test
    @DisplayName("Should allow multi-vehicle split-shifts without time overlap on the same weekday")
    void shouldAllowMultiVehicleSplitShiftsWithoutOverlap() {
        MotoristaHorarioRecord leg1 = new MotoristaHorarioRecord(
                1L, 1L, "ABC1D23", "Padrao", 10L, "Linha 3301 - 10", "06:00", "11:30", List.of("SEG"), "11:30", "12:30"
        );
        MotoristaHorarioRecord leg2 = new MotoristaHorarioRecord(
                2L, 2L, "XYZ9A87", "Padrao", 10L, "Linha 3301 - 10", "12:30", "16:00", List.of("SEG"), "11:30", "12:30"
        );

        MotoristaRecord record = new MotoristaRecord(
                null,
                "Carlos Split",
                "99988877766",
                "CNH-8888888",
                LocalDate.now().plusYears(2),
                "carlossplit",
                "11977776666",
                1L,
                "Cit Mobi",
                "AGUARDANDO",
                List.of(leg1, leg2)
        );

        when(operadorRepository.findById(1L)).thenReturn(Optional.of(sampleOperador1));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(sampleVeiculo1));
        when(veiculoRepository.findByPlaca("XYZ9A87")).thenReturn(Optional.of(sampleVeiculo2));
        when(linhaRepository.findById(10L)).thenReturn(Optional.of(sampleLinha));

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.createMotorista(record, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(veiculoEscalaRepository, times(2)).save(any(VeiculoEscala.class));
        verify(motoristaHoraRepository, times(2)).save(any(MotoristaHora.class));
    }

    @Test
    @DisplayName("Should reject schedule when time intervals overlap on the same weekday")
    void shouldRejectOverlappingShiftsOnSameWeekday() {
        MotoristaHorarioRecord leg1 = new MotoristaHorarioRecord(
                1L, 1L, "ABC1D23", "Padrao", 10L, "Linha 3301 - 10", "06:00", "12:00", List.of("SEG")
        );
        MotoristaHorarioRecord leg2 = new MotoristaHorarioRecord(
                2L, 2L, "XYZ9A87", "Padrao", 10L, "Linha 3301 - 10", "11:00", "16:00", List.of("SEG")
        );

        MotoristaRecord record = new MotoristaRecord(
                null,
                "Carlos Conflict",
                "99988877766",
                "CNH-8888888",
                LocalDate.now().plusYears(2),
                "carlosconflict",
                "11977776666",
                1L,
                "Cit Mobi",
                "AGUARDANDO",
                List.of(leg1, leg2)
        );

        when(operadorRepository.findById(1L)).thenReturn(Optional.of(sampleOperador1));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.createMotorista(record, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Conflito de horários"));
    }

    @Test
    @DisplayName("Should transfer driver to new operator by Admin, clearing old schedules and unassigning vehicle slots")
    void shouldTransferDriverOperatorSuccessfullyByAdmin() {
        String authHeader = "Bearer admin-token";
        when(tokenService.getFlagReguladorFromToken(authHeader)).thenReturn("S");

        when(usuarioRepository.findById(sampleDriver.getId())).thenReturn(Optional.of(sampleDriver));
        when(viagemRepository.findFirstByMotorista_IdAndStatus(sampleDriver.getId(), ViagemStatus.EM_ANDAMENTO))
                .thenReturn(Optional.empty());
        when(operadorRepository.findById(2L)).thenReturn(Optional.of(sampleOperador2));

        VeiculoEscala oldSlot = new VeiculoEscala(sampleVeiculo1, sampleLinha, sampleDriver, "SEGUNDA", LocalTime.of(6, 0), LocalTime.of(14, 0));
        when(veiculoEscalaRepository.findByMotorista_Id(sampleDriver.getId())).thenReturn(List.of(oldSlot));
        when(motoristaHoraRepository.findByMotorista_Id(sampleDriver.getId())).thenReturn(Collections.emptyList());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.transferDriverOperator(
                sampleDriver.getId().toString(), 2L, authHeader
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200", response.getBody().status());
        assertNull(oldSlot.getMotorista(), "Old vehicle slot should be unassigned with motorista = null");
        assertEquals(sampleOperador2, sampleDriver.getOperador(), "Driver operator should be updated to target operator");
    }

    @Test
    @DisplayName("Should reject operator transfer when driver has active trip in progress")
    void shouldRejectOperatorTransferWhenTripIsActive() {
        String authHeader = "Bearer admin-token";
        when(tokenService.getFlagReguladorFromToken(authHeader)).thenReturn("S");

        when(usuarioRepository.findById(sampleDriver.getId())).thenReturn(Optional.of(sampleDriver));
        Viagem activeTrip = new Viagem();
        activeTrip.setStatus(ViagemStatus.EM_ANDAMENTO);
        when(viagemRepository.findFirstByMotorista_IdAndStatus(sampleDriver.getId(), ViagemStatus.EM_ANDAMENTO))
                .thenReturn(Optional.of(activeTrip));

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.transferDriverOperator(
                sampleDriver.getId().toString(), 2L, authHeader
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("viagem em andamento"));
    }

    @Test
    @DisplayName("Should reject operator transfer when caller is not an admin or regulator")
    void shouldRejectOperatorTransferForNonAdmin() {
        String authHeader = "Bearer user-token";
        when(tokenService.getFlagReguladorFromToken(authHeader)).thenReturn("N");
        when(tokenService.getRoleFromToken(authHeader)).thenReturn("ROLE_USER");

        ResponseEntity<GenericResponse<MotoristaRecord>> response = motoristaService.transferDriverOperator(
                sampleDriver.getId().toString(), 2L, authHeader
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("403", response.getBody().status());
    }
}
