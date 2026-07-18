package neo.com.br.CitMobi.controller.veiculo;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.VeiculoRecord;
import neo.com.br.CitMobi.services.VeiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Veiculos", description = "Controller to manage Vehicles")
@PreAuthorize("!hasRole('MOTORISTA')")
@AllArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @GetMapping("/veiculos")
    public ResponseEntity<GenericResponse<List<VeiculoRecord>>> getVeiculos() {
        return veiculoService.getAllVeiculos();
    }

    @PostMapping("/veiculos")
    public ResponseEntity<GenericResponse<VeiculoRecord>> createVeiculo(@RequestBody VeiculoRecord dto) {
        return veiculoService.createVeiculo(dto);
    }

    @PutMapping("/veiculos/{plate}")
    public ResponseEntity<GenericResponse<VeiculoRecord>> updateVeiculo(
            @PathVariable String plate,
            @RequestBody VeiculoRecord dto) {
        return veiculoService.updateVeiculo(plate, dto);
    }

    @DeleteMapping("/veiculos/{plate}")
    public ResponseEntity<GenericResponse<Void>> deleteVeiculo(@PathVariable String plate) {
        return veiculoService.deleteVeiculo(plate);
    }
}
