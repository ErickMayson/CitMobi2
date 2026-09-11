package neo.com.br.CitMobi.models.records.viagem;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ViagemRecord(
        Long id,
        UUID motoristaId,
        String motoristaNome,
        Long veiculoId,
        String veiculoPlaca,
        String veiculoCodigo,
        Long operadorId,
        String operadorNome,
        Long linhaId,
        String linhaCodigo,
        String linhaDescricao,
        Long rotaId,
        String rotaPrefixo,
        String sentido,
        OffsetDateTime dataInicio,
        OffsetDateTime dataFim,
        String status
) {}
