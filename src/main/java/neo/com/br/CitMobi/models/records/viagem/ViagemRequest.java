package neo.com.br.CitMobi.models.records.viagem;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ViagemRequest(
        UUID motoristaId,
        @NotNull(message = "O ID do veículo é obrigatório") Long veiculoId,
        @NotNull(message = "O ID da linha é obrigatório") Long linhaId,
        @NotNull(message = "O ID da rota é obrigatório") Long rotaId
) {}
