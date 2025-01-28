package neo.com.br.CitMobi.models.records.linha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaOperador;
import neo.com.br.CitMobi.models.linha.LinhaOperadorId;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;


public record LinhaOperadorRecord(
        @NotBlank(message = "linhaId is required")
        String linhaId,
        @NotBlank(message = "linhaAtendimento is required")
        String linhaAtendimento,
        @NotNull(message = "municipio is required")
        Long municipio,
        OperadorRecord operador
) {

    public LinhaOperador toLinhaOperador() {
        return new LinhaOperador(new LinhaOperadorId(
                safeTrimAndUppercase(linhaId),
                safeTrimAndUppercase(linhaAtendimento),
                municipio,
                operador != null ? new Operador(
                        safeTrimAndUppercase(operador.cnpj()),
                        safeTrimAndUppercase(operador.razao()))
                        : null
                ));
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
                '}';
    }
}
