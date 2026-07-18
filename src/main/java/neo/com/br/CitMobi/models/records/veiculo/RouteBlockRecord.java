package neo.com.br.CitMobi.models.records.veiculo;

import java.util.List;

public record RouteBlockRecord(
        String routeName,
        String startTime,
        String endTime,
        List<String> days
) {
}
