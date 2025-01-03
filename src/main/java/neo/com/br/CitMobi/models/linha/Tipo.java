package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "T_LIN_TIPO")
@Data
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_TIPO_ID")
    private Long tipoId;

    @Column(name = "LIN_TIPO_DESCRICAO")
    private String tipoDesc;
}
