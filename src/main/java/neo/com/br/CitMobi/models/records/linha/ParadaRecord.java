package neo.com.br.CitMobi.models.records.linha;

public record ParadaRecord(

        String paradaId,
        String logradouro,
        String numero,
        String longitude,
        String latitude,
        String municipioCod,
        String ufSigla,
        String tipoId
) {}
