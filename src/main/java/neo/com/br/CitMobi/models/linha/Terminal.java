package neo.com.br.CitMobi.models.linha;

import lombok.Data;
import jakarta.persistence.*;
import neo.com.br.CitMobi.models.ibge.UF;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "T_LIN_TERMINAL")
public class Terminal {

    @EmbeddedId
    private TerminalId terminalId;  // This will now be your primary key

    @Column(name = "LIN_TERMINAL_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "LIN_TERMINAL_NUMERO", nullable = false, length = 255)
    private String numero;

    @Column(name = "LIN_TERMINAL_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @Column(name = "LIN_TERMINAL_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @ManyToOne
    @JoinColumn(name = "GLB_UF_SIGLA", nullable = false)
    private UF uf; // Assuming UF is a separate entity

    @ManyToOne
    @JoinColumn(name = "LIN_TIPO_ID", nullable = false)
    private Tipo tipo; // Assuming Tipo is a separate entity
}

