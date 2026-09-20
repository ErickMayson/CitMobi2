package neo.com.br.CitMobi.models.records.response;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.linha.LinhaComRotasRecord;

import java.util.List;

public record LinhaDetalhesResponse(
        LinhaComRotasRecord linha,
        List<Operador> operadores
) {
}
