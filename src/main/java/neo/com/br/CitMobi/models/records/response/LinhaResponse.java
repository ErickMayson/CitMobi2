package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;

import java.util.List;

public record LinhaResponse(
        Linha linha,
        List<Operador> operadores
) {


}
