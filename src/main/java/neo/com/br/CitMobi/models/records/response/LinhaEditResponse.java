package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.Linha;

public record LinhaEditResponse(
        String status,
        String message,
        Linha LinhaEditada,
        Linha LinhaAnterior
) {


}
