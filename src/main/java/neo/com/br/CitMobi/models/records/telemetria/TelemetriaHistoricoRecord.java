package neo.com.br.CitMobi.models.records.telemetria;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TelemetriaHistoricoRecord(
        Long id,
        Long viagemId,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal velocidade,
        BigDecimal bearing,
        BigDecimal odometer,
        OffsetDateTime dataRegistro
) {}
