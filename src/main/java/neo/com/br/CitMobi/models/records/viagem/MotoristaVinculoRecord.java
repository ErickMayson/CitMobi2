package neo.com.br.CitMobi.models.records.viagem;

import java.util.UUID;

public record MotoristaVinculoRecord(
        UUID motoristaId,
        String motoristaNome,
        Boolean hasViagemAtiva,
        Long viagemId,
        Long veiculoId,
        String veiculoCodigo,
        String veiculoPlaca,
        Long linhaId,
        String linhaCodigo,
        String linhaDescricao,
        Long rotaId,
        String sentido
) {
}
