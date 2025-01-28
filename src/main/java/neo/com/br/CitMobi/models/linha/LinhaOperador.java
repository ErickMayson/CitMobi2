package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_LIN_LINHA_OPERADOR")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LinhaOperador {

    @EmbeddedId
    LinhaOperadorId linhaOperadorId;

    public LinhaOperador(String linhaId,
                 String linhaAtendimento,
                 Long municipio,
                 Operador operador) {
        this.linhaOperadorId = new LinhaOperadorId(linhaId, linhaAtendimento, municipio, operador);
    }


}
