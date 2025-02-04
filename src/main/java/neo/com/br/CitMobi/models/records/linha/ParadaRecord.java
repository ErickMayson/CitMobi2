package neo.com.br.CitMobi.models.records.linha;

import java.math.BigDecimal;

public record ParadaRecord(
        Long paradaId,
        String logradouro,
        String numero,
        BigDecimal longitude,
        BigDecimal latitude,
        Long municipio,
        String ufSigla,
        Long tipoId
) {}
