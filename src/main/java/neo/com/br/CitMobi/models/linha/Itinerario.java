package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.com.br.CitMobi.models.ibge.Municipio;

@Entity
@Table(name = "T_LIN_ITINERARIO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Itinerario {

    @EmbeddedId
    private ItinerarioId itinerarioId;

}
