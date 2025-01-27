package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;

public record RotaRecord(
        Long itinerarioId,
        Parada parada,
        Long sequencia
) {

    public Rota toRota() {
        return new Rota(
                new RotaId(itinerarioId,
                parada,
                sequencia)
        );
    }

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

}
