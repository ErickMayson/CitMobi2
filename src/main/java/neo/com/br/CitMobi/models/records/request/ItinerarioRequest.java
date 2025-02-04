package neo.com.br.CitMobi.models.records.request;

import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;

import java.util.List;

public record ItinerarioRequest(
        List<ItinerarioRecord> itinerarioRecords
) {

}
