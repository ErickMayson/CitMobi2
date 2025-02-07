package neo.com.br.CitMobi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.request.ItinerarioRequest;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.ItinerarioResponse;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.services.ItinerarioService;
import neo.com.br.CitMobi.services.LinhaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Tag(name = "Rota", description = "Controller to manage rotas.")
public class ItinerarioController {
    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "Criado"),
//            @ApiResponse(code = 200, message = "Retorna o status passada na Path"),
//            @ApiResponse(code = 401, message = "Nâo tem permissão"),
//            @ApiResponse(code = 403, message = "Nâo tem permissâo para acessar o Recurso"),
//            @ApiResponse(code = 404, message = "Não localizada"),
//            @ApiResponse(code = 500, message = "Erro inesperado no Servidor")
//    })

    @RequestMapping(method = RequestMethod.GET, value = "/itinerario", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<ItinerarioResponse>> getItinerario(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio) {

        // Log the parameters for debugging
        System.out.println("municipio: " + municipio);
        System.out.println("linha: " + linha);
        System.out.println("atendimento: " + atendimento);

        return itinerarioService.getItinerario(linha, atendimento, municipio);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/itinerario", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<ItinerarioResponse>> createItinerario(
            @RequestParam String linha,
            @RequestParam String atendimento,
            @RequestParam String municipio,
            @RequestBody ItinerarioRecord itinerario) {

        // Log the parameters for debugging
        System.out.println("municipio: " + municipio);
        System.out.println("linha: " + linha);
        System.out.println("atendimento: " + atendimento);

        return itinerarioService.createItinerario(linha, atendimento, municipio, itinerario);
    }
}
