package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.OperadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperadorServiceTest {

    @Mock
    private OperadorRepository operadorRepository;

    @InjectMocks
    private OperadorService operadorService;

    private Operador operador1;
    private Operador operador2;

    @BeforeEach
    void setUp() {
        operador1 = new Operador("31974104000120", "VIACAO METROPOLE PAULISTA S/A", "N");
        operador1.setId(1L);

        operador2 = new Operador("60870847000159", "VIACAO GATO PRETO LTDA", "N");
        operador2.setId(2L);
    }

    @Test
    @DisplayName("Should return list of all operators wrapped in GenericResponse")
    void shouldGetAllOperadoresSuccessfully() {
        when(operadorRepository.findAll()).thenReturn(List.of(operador1, operador2));

        ResponseEntity<GenericResponse<List<OperadorRecord>>> response = operadorService.getAllOperadores();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(2, response.getBody().data().size());
        assertEquals("31974104000120", response.getBody().data().get(0).cnpj());
        assertEquals("VIACAO METROPOLE PAULISTA S/A", response.getBody().data().get(0).razaoSocial());
        assertEquals("VIACAO GATO PRETO LTDA", response.getBody().data().get(1).razaoSocial());
    }
}
