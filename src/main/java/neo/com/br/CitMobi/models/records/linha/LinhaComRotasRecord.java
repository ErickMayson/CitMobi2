package neo.com.br.CitMobi.models.records.linha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Operador;

import java.util.List;

public record LinhaComRotasRecord(
        Long id,
        String codigoLinha,
        String atendimento,
        String linhaDescricao,
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        Municipio municipio,
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        Operador operador,
        String flagIntermunicipal,
        String flagMetro,
        String flagTrem,
        String flagAtiva,
        List<RotaRecord> rotas
) {
}
