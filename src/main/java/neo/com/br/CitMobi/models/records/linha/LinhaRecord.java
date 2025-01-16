package neo.com.br.CitMobi.models.records.linha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;

public record LinhaRecord(
        @NotBlank(message = "linhaId is required")
        String linhaId,
        @NotBlank(message = "linhaAtendimento is required")
        String linhaAtendimento,
        @NotNull(message = "municipio is required")
        Long municipio,
        @NotBlank(message = "Adicionar os prefixos da linha.")
        String linhaDescricao,
        @NotBlank(message = "cnpjOperador is required")
        Operador operador,
        @NotBlank(message = "flagIntermunicipal is required")
        String flagIntermunicipal,
        @NotBlank(message = "flagMetro is required")
        String flagMetro,
        @NotBlank(message = "flagTrem is required")
        String flagTrem
) {
        // Envia logo o objeto do Operador
    public Linha toLinha() {
        return new Linha(linhaId, linhaAtendimento, municipio, linhaDescricao, operador, flagIntermunicipal, flagMetro, flagTrem);
    }

    @Override
    public String toString() {
        return "LinhaRecord{" +
                "linhaId='" + linhaId + '\'' +
                ", linhaAtendimento='" + linhaAtendimento + '\'' +
                ", municipio=" + municipio +
                ", linhaDescricao='" + linhaDescricao + '\'' +
                ", cnpjOperador='" + cnpjOperador + '\'' +
                ", flagIntermunicipal='" + flagIntermunicipal + '\'' +
                ", flagMetro='" + flagMetro + '\'' +
                ", flagTrem='" + flagTrem + '\'' +
                '}';
    }
}
