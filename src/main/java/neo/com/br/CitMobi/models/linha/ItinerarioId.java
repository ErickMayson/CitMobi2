package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(name = "LIN_ROTA_ID")
    private Long rotaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotBlank
    @JoinColumn(name = "LIN_PARADA_ID")
    private Parada parada;

    @NotBlank
    @Column(name = "LIN_ITINERARIO_SEQUENCIA", nullable = false)
    private Long sequencia;

}
