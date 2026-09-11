package neo.com.br.CitMobi.models.records.telemetria;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TelemetriaPingRequest(
        @NotNull(message = "O ID da viagem é obrigatório") Long viagemId,
        @NotNull(message = "A latitude é obrigatória") BigDecimal latitude,
        @NotNull(message = "A longitude é obrigatória") BigDecimal longitude,
        BigDecimal velocidade,
        BigDecimal bearing,
        BigDecimal odometer,
        Integer sequenciaParadaAtual,
        String statusParadaAtual,
        Long paradaId
) {}
