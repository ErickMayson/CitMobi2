package neo.com.br.CitMobi.models.records.linha;

import java.util.List;

public record ItinerarioRecord(
        Long itinerarioId,
        List<ParadaRecord> paradas
) {
}
