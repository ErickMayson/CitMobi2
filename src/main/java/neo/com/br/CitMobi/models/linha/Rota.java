package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "T_LIN_ROTA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Rota {

    @EmbeddedId
    public RotaId rotaId;

}
