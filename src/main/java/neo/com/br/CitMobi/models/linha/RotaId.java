package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RotaId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_ITINERARIO_ID", insertable = false, updatable = false)
    private Itinerario itinerario;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotBlank
    @JoinColumn(name = "LIN_PARADA_ID")
    private Parada LIN_PARADA_ID;

    @NotBlank
    @Column(name = "LIN_ROTA_SEQUENCIA", nullable = false)
    private Long LIN_ROTA_SEQUENCIA;

}
