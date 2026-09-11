package neo.com.br.CitMobi.controller.viagem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.viagem.ViagemRecord;
import neo.com.br.CitMobi.models.records.viagem.ViagemRequest;
import neo.com.br.CitMobi.services.ViagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/viagens")
@CrossOrigin(value = "*")
@Tag(name = "Viagens", description = "Controller to manage Operational Driver Trip Sessions")
@AllArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @PostMapping("/iniciar")
    @Operation(summary = "Inicia uma nova viagem operacional para um motorista e veículo")
    public ResponseEntity<GenericResponse<ViagemRecord>> iniciarViagem(
            @Valid @RequestBody ViagemRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return viagemService.iniciarViagem(request, authHeader);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finaliza uma viagem operacional em andamento")
    public ResponseEntity<GenericResponse<ViagemRecord>> finalizarViagem(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return viagemService.finalizarViagem(id, authHeader);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma viagem operacional em andamento")
    public ResponseEntity<GenericResponse<ViagemRecord>> cancelarViagem(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return viagemService.cancelarViagem(id, authHeader);
    }

    @GetMapping("/ativas")
    @Operation(summary = "Lista todas as viagens operacionais em andamento (com escopo por operador/regulador)")
    public ResponseEntity<GenericResponse<List<ViagemRecord>>> getViagensAtivas(
            @RequestParam(value = "operadorId", required = false) Long operadorId,
            @RequestParam(value = "linhaId", required = false) Long linhaId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return viagemService.getViagensAtivas(operadorId, linhaId, authHeader);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca detalhes de uma viagem operacional pelo ID")
    public ResponseEntity<GenericResponse<ViagemRecord>> getViagemById(@PathVariable Long id) {
        return viagemService.getViagemById(id);
    }
}
