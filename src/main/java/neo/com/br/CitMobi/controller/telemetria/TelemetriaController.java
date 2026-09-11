package neo.com.br.CitMobi.controller.telemetria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaHistoricoRecord;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaPingRequest;
import neo.com.br.CitMobi.models.records.telemetria.TelemetriaVeiculoRecord;
import neo.com.br.CitMobi.services.TelemetriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/api/telemetria")
@CrossOrigin(value = "*")
@Tag(name = "Telemetria", description = "Controller to ingest GPS telemetry and query real-time vehicle positions")
@AllArgsConstructor
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    @PostMapping("/ping")
    @Operation(summary = "Ingere coordenadas de GPS/telemetria de um veículo em viagem ativa (App Motorista / AVL)")
    public ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> registrarPing(
            @Valid @RequestBody TelemetriaPingRequest request) {
        return telemetriaService.registrarPing(request);
    }

    @GetMapping("/veiculos")
    @Operation(summary = "Retorna as posições em tempo real de todos os veículos ativos para plotagem no mapa do frontend")
    public ResponseEntity<GenericResponse<List<TelemetriaVeiculoRecord>>> getVeiculosAtivos(
            @RequestParam(value = "linhaId", required = false) Long linhaId,
            @RequestParam(value = "operadorId", required = false) Long operadorId,
            @RequestParam(value = "municipio", required = false) Long municipio,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return telemetriaService.getVeiculosAtivos(linhaId, operadorId, municipio, authHeader);
    }

    @GetMapping("/veiculos/{veiculoId}")
    @Operation(summary = "Retorna a última posição de telemetria de um veículo específico")
    public ResponseEntity<GenericResponse<TelemetriaVeiculoRecord>> getVeiculoPosicao(
            @PathVariable Long veiculoId) {
        return telemetriaService.getVeiculoPosicao(veiculoId);
    }

    @GetMapping("/viagens/{viagemId}/historico")
    @Operation(summary = "Retorna o histórico cronológico de coordenadas (breadcrumbs) de uma viagem para auditoria e replay de trajeto")
    public ResponseEntity<GenericResponse<List<TelemetriaHistoricoRecord>>> getHistoricoViagem(
            @PathVariable Long viagemId) {
        return telemetriaService.getHistoricoViagem(viagemId);
    }
}
