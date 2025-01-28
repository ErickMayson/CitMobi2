package neo.com.br.CitMobi.models.records.linha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;


public record LinhaRecord(
        @NotBlank(message = "linhaId is required")
        String linhaId,
        @NotBlank(message = "linhaAtendimento is required")
        String linhaAtendimento,
        @NotNull(message = "municipio is required")
        Long municipio,
        OperadorRecord operador,
        @NotBlank(message = "Adicionar os prefixos da linha.")
        String linhaDescricao,
        @NotBlank(message = "flagIntermunicipal is required")
        String flagIntermunicipal,
        @NotBlank(message = "flagMetro is required")
        String flagMetro,
        @NotBlank(message = "flagTrem is required")
        String flagTrem,
        String flagAtiva
) {

    public Linha toLinha() {
            return new Linha(
                    safeTrimAndUppercase(linhaId),
                    safeTrimAndUppercase(linhaAtendimento),
                    municipio,
                    safeTrimAndUppercase(linhaDescricao),
                    safeTrimAndUppercase(flagIntermunicipal),
                    safeTrimAndUppercase(flagMetro),
                    safeTrimAndUppercase(flagTrem),
                    safeTrimAndUppercase(flagAtiva)
            );
        }

    private String safeTrimAndUppercase(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    @Override
    public String toString() {
        return "LinhaRecord{" +
                "linhaId='" + linhaId + '\'' +
                ", linhaAtendimento='" + linhaAtendimento + '\'' +
                ", municipio=" + municipio +
                ", operador=" + operador +
                ", linhaDescricao='" + linhaDescricao + '\'' +
                ", flagIntermunicipal='" + flagIntermunicipal + '\'' +
                ", flagMetro='" + flagMetro + '\'' +
                ", flagTrem='" + flagTrem + '\'' +
                '}';
    }
}
