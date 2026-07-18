package neo.com.br.CitMobi.models.records.veiculo;

import java.util.List;

public record VeiculoRecord(
        String id,
        String plate,
        String model,
        String type,
        Integer capacity,
        String status,
        String garage,
        List<RouteBlockRecord> routes,
        List<DriverBlockRecord> drivers
) {
}
