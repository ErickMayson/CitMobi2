package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.linha.RotaRecord;


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

    public RotaRecord toRecord() {
        return new RotaRecord(
                rotaId.getItinerario(),
                rotaId.getParada(),
                rotaId.getSequencia()
        );
    }

}
