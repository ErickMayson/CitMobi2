package neo.com.br.CitMobi.controller.veiculo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.GaragemRecord;
import neo.com.br.CitMobi.services.GaragemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Garagens", description = "Controller to manage and retrieve vehicle garages")
@PreAuthorize("!hasRole('MOTORISTA')")
@AllArgsConstructor
public class GaragemController {

    private final GaragemService garagemService;

    @GetMapping(value = "/garagens", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lista todas as garagens cadastradas com filtros opcionais por operadora ou município")
    public ResponseEntity<GenericResponse<List<GaragemRecord>>> getAllGaragens(
            @RequestParam(required = false) Long operadorId,
            @RequestParam(required = false) Long municipio) {
        return garagemService.getAllGaragens(operadorId, municipio);
    }
}
