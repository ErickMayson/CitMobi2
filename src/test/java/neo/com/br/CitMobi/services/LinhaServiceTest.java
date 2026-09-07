package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.ibge.MunicipioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinhaServiceTest {

    @Mock
    private LinhaRepository linhaRepository;

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private MunicipioRepository municipioRepository;

    @InjectMocks
    private LinhaService linhaService;

    private Linha sampleLinha;
    private Operador sampleOperador;
    private Municipio sampleMunicipio;

    @BeforeEach
    void setUp() {
        sampleOperador = new Operador("01234567890123", "Cit Mobi Operacoes", "S");
        sampleOperador.setId(1L);

        sampleMunicipio = new Municipio();
        sampleMunicipio.setCodIbge(3550308L);
        sampleMunicipio.setNome("São Paulo");
        sampleMunicipio.setUf("SP");

        sampleLinha = new Linha("3301", "10", sampleMunicipio, sampleOperador, "Term. Amaral Gurgel / Term. Pq. D. Pedro II", "N", "N", "N", "S");
        sampleLinha.setId(100L);
    }

    @Test
    @DisplayName("Should return 200 and LinhaResponse when linha is found by codigo, atendimento and municipio")
    void shouldReturnLinhaWhenFound() {
        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));

        ResponseEntity<GenericResponse<LinhaResponse>> response = linhaService.getLinha("3550308", "3301", "10");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals("3301", response.getBody().data().linha().getCodigoLinha());
        assertFalse(response.getBody().data().operadores().isEmpty());
    }

    @Test
    @DisplayName("Should return 404 when linha is not found")
    void shouldReturn404WhenNotFound() {
        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("9999", "10", 3550308L))
                .thenReturn(Optional.empty());

        ResponseEntity<GenericResponse<LinhaResponse>> response = linhaService.getLinha("3550308", "9999", "10");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().status());
    }

    @Test
    @DisplayName("Should create a new Linha with surrogate key successfully")
    void shouldCreateLinhaSuccessfully() {
        LinhaRecord record = new LinhaRecord(
                "4001",
                "10",
                3550308L,
                new OperadorRecord("01234567890123", "Cit Mobi"),
                "Nova Linha Expressa",
                "N",
                "S",
                "N",
                "S"
        );

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("4001", "10", 3550308L))
                .thenReturn(Optional.empty());
        when(municipioRepository.findById(3550308L)).thenReturn(Optional.of(sampleMunicipio));
        when(operadorRepository.findByCnpj("01234567890123")).thenReturn(Optional.of(sampleOperador));
        when(linhaRepository.save(any(Linha.class))).thenAnswer(invocation -> {
            Linha saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        ResponseEntity<GenericResponse<LinhaResponse>> response = linhaService.createNewLinha(record);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("201", response.getBody().status());
        assertNotNull(response.getBody().data().linha().getId());
        assertEquals("4001", response.getBody().data().linha().getCodigoLinha());
    }

    @Test
    @DisplayName("Should return 406 when creating a Linha that already exists")
    void shouldReturn406WhenLinhaExists() {
        LinhaRecord record = new LinhaRecord(
                "3301",
                "10",
                3550308L,
                new OperadorRecord("01234567890123", "Cit Mobi"),
                "Linha Repetida",
                "N",
                "N",
                "N",
                "S"
        );

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));

        ResponseEntity<GenericResponse<LinhaResponse>> response = linhaService.createNewLinha(record);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("406", response.getBody().status());
    }

    @Test
    @DisplayName("Should edit an existing Linha successfully")
    void shouldEditLinhaSuccessfully() {
        LinhaRecord editRecord = new LinhaRecord(
                "3301",
                "10",
                3550308L,
                new OperadorRecord("01234567890123", "Cit Mobi"),
                "Linha Descricao Atualizada",
                "S",
                "S",
                "N",
                "S"
        );

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));
        when(linhaRepository.save(any(Linha.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<GenericResponse<LinhaEditResponse>> response = linhaService.editLinha(editRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals("Linha Descricao Atualizada", response.getBody().data().linhaAtualizada().linha().getLinhaDescricao());
    }
}
