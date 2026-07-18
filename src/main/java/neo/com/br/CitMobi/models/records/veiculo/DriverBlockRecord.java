package neo.com.br.CitMobi.models.records.veiculo;

import java.util.List;

public record DriverBlockRecord(
        String name,
        String startTime,
        String endTime,
        List<String> days
) {
}
