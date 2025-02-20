package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.linha.Rota;

public record RotaRecord(
        String linhaId,
        String linhaAtendimento,
        String prefixo,
        Long municipio,
        String linhaSentido,
        ItinerarioRecord itinerario
) {
    public Rota toRota() {
        return new Rota(
                safeTrimAndUppercase(linhaId),
                safeTrimAndUppercase(linhaAtendimento),
                safeTrimAndUppercase(prefixo),
                municipio,
                safeTrimAndUppercase(linhaSentido),
                itinerario.toRotaList()
        );
    }
    // Posso criar itinerarios sem rota definida, entao esse constructor se faz necessario.
    public Rota toRotaNoItinerario() {
        return new Rota(
                safeTrimAndUppercase(linhaId),
                safeTrimAndUppercase(linhaAtendimento),
                safeTrimAndUppercase(prefixo),
                municipio,
                safeTrimAndUppercase(linhaSentido)
        );
    }

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}
