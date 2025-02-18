package neo.com.br.CitMobi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.RotaResponse;
import neo.com.br.CitMobi.services.RotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Tag(name = "Itinerario", description = "Controller to manage rotas.")
public class RotaController {
    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "Criado"),
//            @ApiResponse(code = 200, message = "Retorna o status passada na Path"),
//            @ApiResponse(code = 401, message = "Nâo tem permissão"),
//            @ApiResponse(code = 403, message = "Nâo tem permissâo para acessar o Recurso"),
//            @ApiResponse(code = 404, message = "Não localizada"),
//            @ApiResponse(code = 500, message = "Erro inesperado no Servidor")
//    })

    @RequestMapping(method = RequestMethod.GET, value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<RotaResponse>> getRota(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio) {

        // Log the parameters for debugging
        System.out.println("municipio: " + municipio);
        System.out.println("linha: " + linha);
        System.out.println("atendimento: " + atendimento);

        return rotaService.getRota(linha, atendimento, municipio);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rotas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<RotaResponse>> createRota(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio,
            @RequestBody RotaRecord itinerario) {

        // Log the parameters for debugging
        System.out.println("municipio: " + municipio);
        System.out.println("linha: " + linha);
        System.out.println("atendimento: " + atendimento);

        return rotaService.createRota(linha, atendimento, municipio, itinerario);
    }
}
