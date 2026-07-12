package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "T_USU_MOTORISTAHORA")
@AllArgsConstructor
@NoArgsConstructor
public class MotoristaHora {

    @EmbeddedId
    private MotoristaHoraId motoristaHoraId;

    public MotoristaHora(String usuarioCpf, String diaSemana, String horaInicio, 
                         String horaFim, String pausaInicio, String pausaFim) {
        this.motoristaHoraId = new MotoristaHoraId(usuarioCpf, diaSemana, 
                                                   horaInicio, horaFim, pausaInicio, pausaFim);
    }
}