package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record RotaRecord(
            Long itinerarioId,
            List<Parada> paradas
) {

    public List<Rota> toRotaList() {
        long sequencia = 0L;
        List<Rota> rotaList = new ArrayList<>();
        for (Parada parada : paradas) {
            RotaId rotaId = new RotaId(itinerarioId, parada, ++sequencia);
            Rota rota = new Rota();
            rota.setRotaId(rotaId);
            rotaList.add(rota);
        }
        return rotaList;
    }


    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

}
