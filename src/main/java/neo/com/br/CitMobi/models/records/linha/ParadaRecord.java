package neo.com.br.CitMobi.models.records.linha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Parada;

import java.math.BigDecimal;

public record ParadaRecord(
        Long paradaId,
        @NotBlank(message = "Logradouro é um campo obrigatorio")
        String logradouro,
        @NotBlank(message = "numero é um campo obrigatorio")
        String numero,
        String obs,
        @NotNull(message = "Latitude é obrigatório")
        BigDecimal latitude,
        @NotNull(message = "Longitude é obrigatório")
        BigDecimal longitude,
        @NotNull(message = "Municipio é obrigatório")
        Long municipio,
        @NotBlank(message = "UF é obrigatório")
        String ufSigla,
        @NotNull(message = "Tipo é obrigatório")
        Long tipoId,
        String flagAtiva
) {

    public Parada toParada() {
        return new Parada(
                paradaId,
                safeTrimAndUppercase(logradouro),
                safeTrimAndUppercase(numero),
                safeTrimAndUppercase(obs),
                latitude,
                longitude,
                municipio,
                ufSigla,
                tipoId,
                ativaIfNull(flagAtiva)
        );
    }

    private String ativaIfNull(String flagAtiva) {return flagAtiva != null ? flagAtiva : "S";}

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}
