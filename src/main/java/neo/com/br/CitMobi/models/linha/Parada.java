package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.Data;
import neo.com.br.CitMobi.models.ibge.UF;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "T_LIN_PARADA")
public class Parada {

    @EmbeddedId
    private ParadaId paradaId;  // This will now be your primary key

    @Column(name = "LIN_PARADA_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "LIN_PARADA_NUMERO", nullable = false, length = 255)
    private String numero;

    @Column(name = "LIN_PARADA_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @Column(name = "LIN_PARADA_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @ManyToOne
    @JoinColumn(name = "GLB_UF_SIGLA", nullable = false)
    private UF uf;

    @Column(name = "LIN_TIPO_ID", nullable = false, length = 4)
    private Tipo tipo;

}

