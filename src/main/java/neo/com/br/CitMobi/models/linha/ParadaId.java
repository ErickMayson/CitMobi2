package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ParadaId implements Serializable {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lin_parada_id")
    @SequenceGenerator(name = "seq_lin_parada_id", sequenceName = "SEQ_LIN_PARADA_ID", allocationSize = 1)
    @Column(name = "LIN_PARADA_ID", nullable = false)
    private Long linParadaId;

    @Column(name = "GLB_MUNICIPIO_COD", nullable = false)
    private Long municipioCod;

}

