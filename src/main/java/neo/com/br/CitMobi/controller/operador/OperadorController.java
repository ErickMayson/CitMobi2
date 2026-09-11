package neo.com.br.CitMobi.controller.operador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.OperadorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Operadores", description = "Controller to manage and retrieve Concessionaires / Operators")
@PreAuthorize("isAuthenticated()")
@AllArgsConstructor
public class OperadorController {

    private final OperadorService operadorService;

    @GetMapping(value = "/operadores", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista todas as operadoras / concessionárias cadastradas")
    public ResponseEntity<GenericResponse<List<OperadorRecord>>> getAllOperadores() {
        return operadorService.getAllOperadores();
    }
}
