package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "T_LIN_ITINERARIO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Itinerario {

    @EmbeddedId
    public ItinerarioId itinerarioId;

}
