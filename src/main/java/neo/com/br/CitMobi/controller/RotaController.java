package neo.com.br.CitMobi.controller;

import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.ParadaService;
import neo.com.br.CitMobi.services.RotaService;
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
public class RotaController {

    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final RotaService paradaService;

    public RotaController(RotaService paradaService) {
        this.paradaService = paradaService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rota/getRotasPerLine", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<List<RotaRecord>>> getRotasByMunicipio(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam Long municipio) {

        return paradaService.getRotasPerLine(linha, atendimento, municipio);
    }

}
