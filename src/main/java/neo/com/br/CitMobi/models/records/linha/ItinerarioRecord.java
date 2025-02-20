package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.ItinerarioId;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Itinerario;

import java.util.ArrayList;
import java.util.List;

public record ItinerarioRecord(
            Long itinerarioId,
            List<ParadaRecord> paradas
) {

    public List<Itinerario> toRotaList() {
        long sequencia = 0L;
        List<Itinerario> itinerarioList = new ArrayList<>();
        for (ParadaRecord parada : paradas) {
            ItinerarioId itinerarioId = new ItinerarioId(this.itinerarioId, parada.toParada(), ++sequencia);
            Itinerario itinerario = new Itinerario();
            itinerario.setItinerarioId(itinerarioId);
            itinerarioList.add(itinerario);
        }
        return itinerarioList;
    }


    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

}
