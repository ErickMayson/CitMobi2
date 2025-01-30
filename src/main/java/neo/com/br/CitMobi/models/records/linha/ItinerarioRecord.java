package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.linha.Rota;

import java.util.List;

public record ItinerarioRecord(
        String linhaId,
        String linhaAtendimento,
        String prefixo,
        Long municipio,
        String linhaSentido,
        Rota rota
) {
    public Itinerario toItinerario() {
        return new Itinerario(
                safeTrimAndUppercase(linhaId),
                safeTrimAndUppercase(linhaAtendimento),
                safeTrimAndUppercase(prefixo),
                municipio,
                safeTrimAndUppercase(linhaSentido),
                rota
        );
    }

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}
