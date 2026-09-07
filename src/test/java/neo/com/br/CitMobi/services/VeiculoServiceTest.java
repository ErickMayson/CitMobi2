package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.VeiculoRecord;
import neo.com.br.CitMobi.models.veiculo.*;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.repository.ibge.MunicipioRepository;
import neo.com.br.CitMobi.repository.veiculo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoModeloRepository veiculoModeloRepository;

    @Mock
    private GaragemRepository garagemRepository;

    @Mock
    private LinhaPlacaRepository linhaPlacaRepository;

    @Mock
    private MotoristaPlacaRepository motoristaPlacaRepository;

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LinhaRepository linhaRepository;

    @Mock
    private MunicipioRepository municipioRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo sampleVeiculo;
    private Operador sampleOperador;
    private VeiculoModelo sampleModelo;
    private Garagem sampleGaragem;

    @BeforeEach
    void setUp() {
        sampleOperador = new Operador("01234567890123", "Cit Mobi", "S");
        sampleOperador.setId(1L);

        sampleModelo = new VeiculoModelo("CAIO", "Apache VIP IV", 2, "Padrao");
        sampleModelo.setVeiculoModeloId(1L);

        Municipio municipio = new Municipio();
        municipio.setCodIbge(3550308L);

        sampleGaragem = new Garagem("Garagem Central", sampleOperador, municipio, "Av Teste", "100", "01001000");
        sampleGaragem.setId(1L);

        sampleVeiculo = new Veiculo("ABC1D23", "BUS-01", sampleOperador, sampleModelo, 80, "2020", sampleGaragem);
        sampleVeiculo.setId(1L);
        sampleVeiculo.setStatus(VeiculoStatus.ATIVO);
    }

    @Test
    @DisplayName("Should retrieve all vehicles mapped to VeiculoRecord")
    void shouldGetAllVeiculos() {
        when(veiculoRepository.findAll()).thenReturn(List.of(sampleVeiculo));
        when(linhaPlacaRepository.findByVeiculo_Placa("ABC1D23")).thenReturn(Collections.emptyList());
        when(motoristaPlacaRepository.findByVeiculo_Placa("ABC1D23")).thenReturn(Collections.emptyList());

        ResponseEntity<GenericResponse<List<VeiculoRecord>>> response = veiculoService.getAllVeiculos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(1, response.getBody().data().size());
        assertEquals("ABC1D23", response.getBody().data().get(0).plate());
        assertEquals("ATIVO", response.getBody().data().get(0).status());
    }

    @Test
    @DisplayName("Should create a vehicle successfully")
    void shouldCreateVeiculoSuccessfully() {
        VeiculoRecord record = new VeiculoRecord(
                "BUS-02",
                "XYZ9A87",
                "CAIO Millennium",
                "Padrao",
                85,
                "ATIVO",
                "Garagem Central",
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(veiculoModeloRepository.findAll()).thenReturn(List.of(sampleModelo));
        when(operadorRepository.findAll()).thenReturn(List.of(sampleOperador));
        when(garagemRepository.findAll()).thenReturn(List.of(sampleGaragem));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> {
            Veiculo v = invocation.getArgument(0);
            v.setId(2L);
            return v;
        });

        ResponseEntity<GenericResponse<VeiculoRecord>> response = veiculoService.createVeiculo(record);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("201", response.getBody().status());
        assertEquals("XYZ9A87", response.getBody().data().plate());
    }

    @Test
    @DisplayName("Should update vehicle status and properties")
    void shouldUpdateVeiculoSuccessfully() {
        VeiculoRecord updateRecord = new VeiculoRecord(
                "BUS-01-EDIT",
                "ABC1D23",
                "CAIO Apache VIP IV",
                "Padrao",
                90,
                "EM_MANUTENCAO",
                "Garagem Central",
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(sampleVeiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<GenericResponse<VeiculoRecord>> response = veiculoService.updateVeiculo("ABC1D23", updateRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals("MANUTENCAO", response.getBody().data().status());
        assertEquals(90, response.getBody().data().capacity());
    }

    @Test
    @DisplayName("Should delete vehicle and its associated schedules")
    void shouldDeleteVeiculoSuccessfully() {
        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(sampleVeiculo));
        when(linhaPlacaRepository.findByVeiculo_Placa("ABC1D23")).thenReturn(Collections.emptyList());
        when(motoristaPlacaRepository.findByVeiculo_Placa("ABC1D23")).thenReturn(Collections.emptyList());

        ResponseEntity<GenericResponse<Void>> response = veiculoService.deleteVeiculo("ABC1D23");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200", response.getBody().status());
        verify(veiculoRepository, times(1)).delete(sampleVeiculo);
    }
}
