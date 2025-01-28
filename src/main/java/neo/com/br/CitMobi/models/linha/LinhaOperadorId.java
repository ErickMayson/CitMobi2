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
public class LinhaOperadorId implements Serializable {

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GLB_OPERADOR_CNPJ")
    private Operador operador;


}
