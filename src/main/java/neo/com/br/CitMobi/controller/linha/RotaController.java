package neo.com.br.CitMobi.controller.linha;

import io.swagger.v3.oas.annotations.tags.Tag;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.RotaResponse;
import neo.com.br.CitMobi.services.RotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Itinerario", description = "Controller to manage rotas.")
public class RotaController {
    private static final Logger logger = LoggerFactory.getLogger(RotaController.class);

    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MOTORISTA')")
    public ResponseEntity<GenericResponse<RotaResponse>> getRota(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio) {

        return rotaService.getRota(linha, atendimento, municipio);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GenericResponse<RotaResponse>> createRota(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio,
            @RequestBody RotaRecord itinerario) {

        return rotaService.createRota(linha, atendimento, municipio, itinerario);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GenericResponse<RotaResponse>> updateRota(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio,
            @RequestBody RotaRecord rotaRecord) {

        return rotaService.updateRota(linha, atendimento, municipio, rotaRecord);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/rotas/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GenericResponse<RotaResponse>> updateRotaById(
            @PathVariable Long id,
            @RequestBody RotaRecord rotaRecord) {

        return rotaService.updateRotaById(id, rotaRecord);
    }
}
