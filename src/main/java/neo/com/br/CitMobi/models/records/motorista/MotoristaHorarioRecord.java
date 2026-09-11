package neo.com.br.CitMobi.models.records.motorista;

import java.util.List;

public record MotoristaHorarioRecord(
        Object id,
        Object veiculoId,
        String veiculoPlaca,
        String veiculoModelo,
        Object rotaId,
        String rotaNome,
        String startTime,
        String endTime,
        List<String> days,
        String pausaInicio,
        String pausaFim
) {
    public MotoristaHorarioRecord(Object id, Object veiculoId, String veiculoPlaca, String veiculoModelo, Object rotaId, String rotaNome, String startTime, String endTime, List<String> days) {
        this(id, veiculoId, veiculoPlaca, veiculoModelo, rotaId, rotaNome, startTime, endTime, days, null, null);
    }
}
