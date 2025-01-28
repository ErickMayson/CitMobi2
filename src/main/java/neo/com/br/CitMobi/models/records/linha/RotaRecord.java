package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;

public record RotaRecord(
        Itinerario itinerario,
        Parada parada,
        Long sequencia
) {

    public Rota toRota() {
        RotaId rotaId = new RotaId(itinerario, parada, sequencia);

        // Now create a Rota and set the RotaId
        Rota rota = new Rota();
        rota.setRotaId(rotaId); // Set the embedded ID
        return rota;
    }

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

}
