package neo.com.br.CitMobi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.services.LinhaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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

    @RequestMapping(method = RequestMethod.GET, value = "/linha/getLinha", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<LinhaResponse>> getLinha(
            @RequestParam String municipio,
            @RequestParam String linha,
            @RequestParam String atendimento) {

        // Log the parameters for debugging
        System.out.println("municipio: " + municipio);
        System.out.println("linha: " + linha);
        System.out.println("atendimento: " + atendimento);

        return linhaService.getLinha(municipio, linha, atendimento);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/linha/createLinha", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<LinhaResponse>> createLinha(@Valid @RequestBody LinhaRecord linha, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            logger.error("Validation errors: {}", errorMessages);

            LinhaResponse linhaResponse = new LinhaResponse(null, null);
            GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("400", "Validation errors: " + errorMessages, linhaResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        return linhaService.createNewLinha(linha);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/linha/editLinha", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GenericResponse<LinhaEditResponse>> editLinha(@RequestBody LinhaRecord linha, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            logger.error("Validation errors: {}", errorMessages);

            LinhaEditResponse linhaResponse = new LinhaEditResponse(null, null);
            GenericResponse<LinhaEditResponse> errorResponse = new GenericResponse<>("400", "Validation errors: " + errorMessages, linhaResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        return linhaService.editLinha(linha);
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/linha/addOperador", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<GenericResponse<LinhaResponse>> addOperador(
//            @RequestParam String cnpj,
//            @RequestParam String municipio,
//            @RequestParam String linha,
//            @RequestParam String atendimento) {
//
//        // Log the parameters for debugging
//        System.out.println("cnpj: " + cnpj);
//        System.out.println("municipio: " + municipio);
//        System.out.println("linha: " + linha);
//        System.out.println("atendimento: " + atendimento);
//
//        return linhaService.getLinha(cnpj, municipio, linha, atendimento);
//    }


}