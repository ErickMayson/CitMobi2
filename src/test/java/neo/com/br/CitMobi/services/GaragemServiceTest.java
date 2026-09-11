package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.GaragemRecord;
import neo.com.br.CitMobi.models.veiculo.Garagem;
import neo.com.br.CitMobi.repository.veiculo.GaragemRepository;
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
class GaragemServiceTest {

    @Mock
    private GaragemRepository garagemRepository;

    @InjectMocks
    private GaragemService garagemService;

    private Garagem garagem1;
    private Garagem garagem2;

    @BeforeEach
    void setUp() {
        Operador operador = new Operador("60870847000159", "VIACAO GATO PRETO LTDA", "N");
        operador.setId(3L);

        Municipio municipio = new Municipio();
        municipio.setCodIbge(3550308L);

        garagem1 = new Garagem("Garagem Central", operador, municipio, "Av. Celso Garcia", "1000", "03064000");
        garagem1.setId(1L);

        garagem2 = new Garagem("Garagem Norte", operador, municipio, "Av. Cruzeiro do Sul", "500", "02030000");
        garagem2.setId(2L);
    }

    @Test
    @DisplayName("Should retrieve all garages when no filter is provided")
    void shouldGetAllGaragensWithoutFilters() {
        when(garagemRepository.findAll()).thenReturn(List.of(garagem1, garagem2));

        ResponseEntity<GenericResponse<List<GaragemRecord>>> response = garagemService.getAllGaragens(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(2, response.getBody().data().size());
        assertEquals("Garagem Central", response.getBody().data().get(0).descricao());
        assertEquals(3L, response.getBody().data().get(0).operadorId());
    }

    @Test
    @DisplayName("Should retrieve garages filtered by operator ID")
    void shouldGetGaragensFilteredByOperador() {
        when(garagemRepository.findByOperador_Id(3L)).thenReturn(List.of(garagem1, garagem2));

        ResponseEntity<GenericResponse<List<GaragemRecord>>> response = garagemService.getAllGaragens(3L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().data().size());
    }

    @Test
    @DisplayName("Should retrieve garages filtered by municipality IBGE code")
    void shouldGetGaragensFilteredByMunicipio() {
        when(garagemRepository.findByMunicipio_CodIbge(3550308L)).thenReturn(List.of(garagem1));

        ResponseEntity<GenericResponse<List<GaragemRecord>>> response = garagemService.getAllGaragens(null, 3550308L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().data().size());
        assertEquals("Garagem Central", response.getBody().data().get(0).descricao());
    }
}
