package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import neo.com.br.CitMobi.models.ibge.Municipio;

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
    @Column(name = "LIN_LINHA_ID")
    private String linhaId;

    @NotBlank
    @Column(name="LIN_LINHA_ATENDIMENTO")
    private String linhaAtendimento;

    @NotNull
    @Column(name = "GLB_MUNICIPIO_COD")
    private Long municipio;

    @NotBlank
    @Column(name = "LIN_ITINERARIO_SENTIDO")
    private String linhaSentido;

    @NotNull
    @Column(name = "LIN_PARADA_ID")
    private Long paradaId;

    @NotNull
    @Column(name = "LIN_ITINERARIO_SEQUENCIA")
    private Integer sequencia;

}
