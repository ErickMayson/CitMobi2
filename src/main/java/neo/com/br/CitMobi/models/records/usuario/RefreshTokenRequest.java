package neo.com.br.CitMobi.models.records.usuario;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank String refreshToken
) {
}
