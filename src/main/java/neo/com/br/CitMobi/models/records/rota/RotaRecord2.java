package neo.com.br.CitMobi.models.records.rota;

import neo.com.br.CitMobi.models.linha.Parada;

import java.util.List;

public record RotaRecord2(
        List<Parada> paradas
) {
}
