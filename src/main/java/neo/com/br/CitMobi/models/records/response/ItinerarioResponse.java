package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.*;

import java.util.List;

public record ItinerarioResponse(
                Itinerario itinerario,
                Rota rota
) {
}
