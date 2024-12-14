package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T_LIN_TIPO")
public class Tipo {

    @Id
    @Column(name = "LIN_TIPO_ID")
    private Long tipoId;

    @Column(name = "LIN_TIPO_DESCRICAO")
    private String tipoDesc;
}
