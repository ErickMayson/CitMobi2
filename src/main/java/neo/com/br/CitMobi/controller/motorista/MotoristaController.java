package neo.com.br.CitMobi.controller.motorista;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.motorista.MotoristaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.services.MotoristaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Motoristas", description = "Controller to manage Drivers and their schedules")
@PreAuthorize("!hasRole('MOTORISTA')")
@AllArgsConstructor
public class MotoristaController {

    private final MotoristaService motoristaService;

    @GetMapping("/motoristas")
    public ResponseEntity<GenericResponse<List<MotoristaRecord>>> getMotoristas(
            @RequestParam(value = "operadorId", required = false) Long operadorId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return motoristaService.getAllMotoristas(operadorId, authHeader);
    }

    @PostMapping("/motoristas")
    public ResponseEntity<GenericResponse<MotoristaRecord>> createMotorista(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MotoristaRecord record) {
        return motoristaService.createMotorista(record, authHeader);
    }

    @PutMapping("/motoristas/{id}")
    public ResponseEntity<GenericResponse<MotoristaRecord>> updateMotorista(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MotoristaRecord record) {
        return motoristaService.updateMotorista(id, record, authHeader);
    }

    @PatchMapping("/motoristas/{id}/operador")
    public ResponseEntity<GenericResponse<MotoristaRecord>> transferOperador(
            @PathVariable String id,
            @RequestParam("novoOperadorId") Long novoOperadorId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return motoristaService.transferDriverOperator(id, novoOperadorId, authHeader);
    }

    @DeleteMapping("/motoristas/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteMotorista(@PathVariable String id) {
        return motoristaService.deleteMotorista(id);
    }
}
