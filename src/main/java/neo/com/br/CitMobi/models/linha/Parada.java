package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.Data;
import neo.com.br.CitMobi.models.ibge.UF;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "T_LIN_PARADA")
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lin_parada_id")
    @SequenceGenerator(name = "seq_lin_parada_id", sequenceName = "SEQ_LIN_PARADA_ID", allocationSize = 1)
    @Column(name = "LIN_PARADA_ID", nullable = false)
    private Long linParadaId;

    @Column(name = "LIN_PARADA_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "LIN_PARADA_NUMERO", nullable = false, length = 255)
    private String numero;

    @Column(name = "LIN_PARADA_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @Column(name = "LIN_PARADA_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @Column(name = "GLB_MUNICIPIO_COD")
    private Long municipio;

    @ManyToOne
    @JoinColumn(name = "GLB_UF_SIGLA", referencedColumnName = "GLB_UF_SIGLA", nullable = false)
    private UF uf;

    @ManyToOne
    @JoinColumn(name = "LIN_TIPO_ID", referencedColumnName = "LIN_TIPO_ID")
    private Tipo tipo;

    public ParadaRecord toRecord() {
        return new ParadaRecord(
                linParadaId,
                logradouro,
                numero,
                longitude,
                latitude,
                municipio,
                uf.getSigla(),
                tipo.getTipoId()
        );
    }

}

