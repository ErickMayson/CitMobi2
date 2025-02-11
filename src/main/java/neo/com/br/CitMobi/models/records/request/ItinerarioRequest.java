package neo.com.br.CitMobi.models.records.request;

import neo.com.br.CitMobi.models.records.linha.RotaRecord;

import java.util.List;

public record ItinerarioRequest(
        List<RotaRecord> rotaRecords
) {

}
