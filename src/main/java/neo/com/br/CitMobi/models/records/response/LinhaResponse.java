package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.Linha;

public record LinhaResponse(
        String status,
        String message,
        Linha linha
) {


}
