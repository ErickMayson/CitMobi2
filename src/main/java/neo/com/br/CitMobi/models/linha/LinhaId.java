package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.ibge.UF;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinhaId implements Serializable {

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

}
