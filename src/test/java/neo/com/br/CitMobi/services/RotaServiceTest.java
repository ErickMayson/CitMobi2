package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.RotaResponse;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.RotaRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RotaServiceTest {

    @Mock
    private ItinerarioRepository itinerarioRepository;

    @Mock
    private ItinerarioService itinerarioService;

    @Mock
    private LinhaRepository linhaRepository;

    @Mock
    private RotaRepository rotaRepository;

    @InjectMocks
    private RotaService rotaService;

    private Linha sampleLinha;
    private Rota sampleRota;

    @BeforeEach
    void setUp() {
        Municipio municipio = new Municipio();
        municipio.setCodIbge(3550308L);

        Operador operador = new Operador("01234567890123", "Cit Mobi", "S");
        operador.setId(1L);

        sampleLinha = new Linha("3301", "10", municipio, operador, "Amaral Gurgel", "N", "N", "N", "S");
        sampleLinha.setId(10L);

        sampleRota = new Rota(sampleLinha, "3301-10", "IDA");
        sampleRota.setId(20L);
    }

    @Test
    @DisplayName("Should return 200 with Rotas and Itinerarios")
    void shouldReturnRotasSuccessfully() {
        Parada parada = new Parada("Rua Teste", "123", "Perto do metro", BigDecimal.valueOf(-46.6), BigDecimal.valueOf(-23.5), 3550308L, "SP", 1L);
        parada.setParadaId(50L);
        parada.setAtiva("S");

        Itinerario itinerario = new Itinerario(sampleRota, parada, 1);
        itinerario.setId(100L);

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));
        when(rotaRepository.findByLinha_Id(10L)).thenReturn(List.of(sampleRota));
        when(itinerarioRepository.findByRota_IdOrderBySequenciaAsc(20L)).thenReturn(List.of(itinerario));

        ResponseEntity<GenericResponse<RotaResponse>> response = rotaService.getRota("3301", "10", "3550308");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("200", response.getBody().status());
        assertEquals(1, response.getBody().data().rotaRecords().size());
        assertEquals(20L, response.getBody().data().rotaRecords().get(0).id());
    }

    @Test
    @DisplayName("Should return 404 when linha does not exist for rota query")
    void shouldReturn404WhenLinhaDoesNotExist() {
        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("9999", "10", 3550308L))
                .thenReturn(Optional.empty());

        ResponseEntity<GenericResponse<RotaResponse>> response = rotaService.getRota("9999", "10", "3550308");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should create new Rota without initial itinerario successfully")
    void shouldCreateRotaWithoutItinerario() {
        RotaRecord newRota = new RotaRecord("3301", "10", "3301-10", 3550308L, "VOLTA", null);

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));
        when(rotaRepository.existsByLinha_IdAndSentido(10L, "VOLTA")).thenReturn(false);
        when(rotaRepository.save(any(Rota.class))).thenAnswer(invocation -> {
            Rota r = invocation.getArgument(0);
            r.setId(21L);
            return r;
        });

        ResponseEntity<GenericResponse<RotaResponse>> response = rotaService.createRota("3301", "10", "3550308", newRota);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("201", response.getBody().status());
    }

    @Test
    @DisplayName("Should return 409 when creating a duplicate route sense")
    void shouldReturn409WhenRouteAlreadyExists() {
        RotaRecord duplicateRota = new RotaRecord("3301", "10", "3301-10", 3550308L, "IDA", null);

        when(linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge("3301", "10", 3550308L))
                .thenReturn(Optional.of(sampleLinha));
        when(rotaRepository.existsByLinha_IdAndSentido(10L, "IDA")).thenReturn(true);

        ResponseEntity<GenericResponse<RotaResponse>> response = rotaService.createRota("3301", "10", "3550308", duplicateRota);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("409", response.getBody().status());
    }
}
