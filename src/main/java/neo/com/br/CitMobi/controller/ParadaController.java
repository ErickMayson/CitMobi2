package neo.com.br.CitMobi.controller;

import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.ItinerarioService;
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
//@Api(value = "Controller que faz a inserção de notas")
public class ParadaController {

    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final ParadaService paradaService;

    public ParadaController(ParadaService paradaService) {
        this.paradaService = paradaService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/parada/getParadasByMunicipio", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadasByMunicipio(
            @RequestParam Long municipio) {

        return paradaService.getParadasByMunicipio(municipio);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/parada/getParadasByLogradouro", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadasByLogradouro(
            @RequestParam String logradouro,
            @RequestParam Long municipio) {

        return paradaService.getParadasByLogradouro(logradouro, municipio);
    }
}
