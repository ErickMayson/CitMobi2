package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.*;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;

import java.util.List;

public record ItinerarioResponse(
                List<ItinerarioRecord> itinerarioRecords
) {
}
