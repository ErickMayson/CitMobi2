package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "T_VEI_MOTORISTAPLACA")
@AllArgsConstructor
@NoArgsConstructor
public class MotoristaPlaca {

    @EmbeddedId
    private MotoristaPlacaId motoristaPlacaId;

    public MotoristaPlaca(String usuarioCpf, String veiculoPlaca, String diaSemana, 
                          String horaInicio, String horaFim) {
        this.motoristaPlacaId = new MotoristaPlacaId(usuarioCpf, veiculoPlaca, 
                                                     diaSemana, horaInicio, horaFim);
    }
}