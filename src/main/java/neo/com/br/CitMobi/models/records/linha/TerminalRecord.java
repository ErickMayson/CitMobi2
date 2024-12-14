package neo.com.br.CitMobi.models.records.linha;

import neo.com.br.CitMobi.models.ibge.UF;
import neo.com.br.CitMobi.models.linha.Tipo;

public record TerminalRecord(
        String nome,
        String logradouro,
        String numero,
        String longitude,
        String latitude,
        String municipioCod,
        String ufSigla,
        String tipoId
) {}
