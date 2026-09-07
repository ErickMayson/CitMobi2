package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.ibge.UF;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "T_LIN_TERMINAL")
@AllArgsConstructor
@NoArgsConstructor
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_TERMINAL_ID")
    private Long id;

    @Column(name = "LIN_TERMINAL_NOME", nullable = false)
    private String nome;

    @Column(name = "LIN_TERMINAL_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "LIN_TERMINAL_NUMERO", nullable = false, length = 255)
    private String numero;

    @Column(name = "LIN_TERMINAL_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @Column(name = "LIN_TERMINAL_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_MUNICIPIO_COD", nullable = false)
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_UF_SIGLA", nullable = false)
    private UF uf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_TIPO_ID", nullable = false)
    private Tipo tipo;
}


