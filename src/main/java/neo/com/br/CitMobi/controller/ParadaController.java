package neo.com.br.CitMobi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.ParadaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Tag(name = "Parada", description = "Controller to manage Paradas (Stops)")
public class ParadaController {

    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final ParadaService paradaService;

    public ParadaController(ParadaService paradaService) {
        this.paradaService = paradaService;
    }

    @GetMapping(value = "/paradas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadas(
            @RequestParam(required = false) String logradouro,
            @RequestParam Long municipio) {

        if (logradouro != null) {
            return paradaService.getParadasByLogradouro(logradouro, municipio);
        } else {
            return paradaService.getParadasByMunicipio(municipio);
        }
    }
}
