package neo.com.br.CitMobi.models.records.linha;

public record RotaRecord(
        Long id,
        String linhaId,
        String linhaAtendimento,
        String prefixo,
        Long municipio,
        String linhaSentido,
        ItinerarioRecord itinerario
) {
    public RotaRecord(
            String linhaId,
            String linhaAtendimento,
            String prefixo,
            Long municipio,
            String linhaSentido,
            ItinerarioRecord itinerario
    ) {
        this(null, linhaId, linhaAtendimento, prefixo, municipio, linhaSentido, itinerario);
    }
}
