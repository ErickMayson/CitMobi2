package neo.com.br.CitMobi.models.records.ibge;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegiaoImediataRecord(
        Long id,
        String nome,
        @JsonProperty("regiao-intermediaria") RegiaoIntermediariaRecord regiaoIntermediaria
) {
}
