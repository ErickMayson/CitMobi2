package neo.com.br.CitMobi.controller.linha;

import io.swagger.v3.oas.annotations.tags.Tag;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.ItinerarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Rota", description = "Controller to manage Itinerarios (stops).")
public class ItinerarioController {

    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/itinerario", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<ItinerarioRecord>>> getRotasByMunicipio(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam Long municipio) {

        return itinerarioService.getItinerariosPerLine(linha, atendimento, municipio);
    }

}
