package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "T_VEI_LINHAPLACA")
@AllArgsConstructor
@NoArgsConstructor
public class LinhaPlaca {

    @EmbeddedId
    private LinhaPlacaId linhaPlacaId;

    public LinhaPlaca(String linhaId, String linhaAtendimento, Long municipio, 
                      String veiculoPlaca, String diaSemana, String horaInicio, String horaFim) {
        this.linhaPlacaId = new LinhaPlacaId(linhaId, linhaAtendimento, municipio, 
                                             veiculoPlaca, diaSemana, horaInicio, horaFim);
    }
}