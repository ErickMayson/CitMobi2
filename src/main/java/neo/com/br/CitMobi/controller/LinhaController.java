package neo.com.br.CitMobi.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.services.LinhaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
//@Api(value = "Controller que faz a inserção de notas")
public class LinhaController {

    private static final Logger logger = LoggerFactory.getLogger(LinhaController.class);

    private final LinhaService linhaService;

    public LinhaController(LinhaService linhaService) {
        this.linhaService = linhaService;
    }

//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "Criado"),
//            @ApiResponse(code = 200, message = "Retorna o status passada na Path"),
//            @ApiResponse(code = 401, message = "Nâo tem permissão"),
//            @ApiResponse(code = 403, message = "Nâo tem permissâo para acessar o Recurso"),
//            @ApiResponse(code = 404, message = "Não localizada"),
//            @ApiResponse(code = 500, message = "Erro inesperado no Servidor")
//    })

    @RequestMapping(method = RequestMethod.POST, value = "/linha/createLinha", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LinhaResponse> createLinha(@RequestBody LinhaRecord linha) {
        if (linha.toString().isEmpty()) {
            LinhaResponse errorResponse = new LinhaResponse("400", "Required fields are missing or invalid.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        return linhaService.createNewLinha(linha);
    }


}