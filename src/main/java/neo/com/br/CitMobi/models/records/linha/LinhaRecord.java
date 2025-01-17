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
        @NotBlank(message = "Adicionar os prefixos da linha.")
        String linhaDescricao,
        OperadorRecord operador,
        @NotBlank(message = "flagIntermunicipal is required")
        String flagIntermunicipal,
        @NotBlank(message = "flagMetro is required")
        String flagMetro,
        @NotBlank(message = "flagTrem is required")
        String flagTrem
) {
        // Envia logo o objeto do Operador
    public Linha toLinha() {
        return new Linha(linhaId.trim().toUpperCase(), linhaAtendimento.trim().toUpperCase(), municipio, linhaDescricao.trim().toUpperCase(),
                operador != null ? new Operador(operador.cnpj(), operador.razao()) : null,
                flagIntermunicipal.trim().toUpperCase(), flagMetro.trim().toUpperCase(), flagTrem.trim().toUpperCase());
    }

    public LinhaRecord normalize() {
        return new LinhaRecord(
                linhaId.trim().toUpperCase(),
                linhaAtendimento.trim().toUpperCase(),
                municipio,
                linhaDescricao.trim().toUpperCase(),
                operador,
                flagIntermunicipal.trim().toUpperCase(),
                flagMetro.trim().toUpperCase(),
                flagTrem.trim().toUpperCase()
        );
    }

    @Override
    public String toString() {
        return "LinhaRecord{" +
                "linhaId='" + linhaId + '\'' +
                ", linhaAtendimento='" + linhaAtendimento + '\'' +
                ", municipio=" + municipio +
                ", linhaDescricao='" + linhaDescricao + '\'' +
                ", operador=" + operador +
                ", flagIntermunicipal='" + flagIntermunicipal + '\'' +
                ", flagMetro='" + flagMetro + '\'' +
                ", flagTrem='" + flagTrem + '\'' +
                '}';
    }
}
