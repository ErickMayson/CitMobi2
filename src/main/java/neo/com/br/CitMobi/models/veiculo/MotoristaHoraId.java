package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import neo.com.br.CitMobi.models.usuario.Usuario;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotoristaHoraId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_USUARIO_CPF")
    private Usuario usuario;

    @NotBlank
    @Column(name = "USU_MOTORISTAHORA_DIASEMANA", length = 30)
    private String diaSemana;

    @Column(name = "USU_MOTORISTAHORA_HORAINICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "USU_MOTORISTAHORA_HORAFIM", nullable = false)
    private LocalTime horaFim;

    @Column(name = "USU_MOTORISTAHORA_PAUSAINICIO", nullable = false)
    private LocalTime pausaInicio;

    @Column(name = "USU_MOTORISTAHORA_PAUSAFIM", nullable = false)
    private LocalTime pausaFim;

    public MotoristaHoraId(String usuarioCpf, String diaSemana, String horaInicio, 
                           String horaFim, String pausaInicio, String pausaFim) {
        this.usuario = new Usuario();
        this.usuario.setCpf(usuarioCpf);
        this.diaSemana = diaSemana;
        this.horaInicio = LocalTime.parse(horaInicio);
        this.horaFim = LocalTime.parse(horaFim);
        this.pausaInicio = LocalTime.parse(pausaInicio);
        this.pausaFim = LocalTime.parse(pausaFim);
    }
}