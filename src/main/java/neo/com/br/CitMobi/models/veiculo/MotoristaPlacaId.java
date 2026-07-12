package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import neo.com.br.CitMobi.models.usuario.Usuario;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotoristaPlacaId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_USUARIO_CPF")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VEI_VEICULO_PLACA")
    private Veiculo veiculo;

    @NotBlank
    @Column(name = "VEI_MOTORISTAPLACA_DIASEMANA", length = 30)
    private String diaSemana;

    @Column(name = "VEI_MOTORISTAPLACA_HORAINICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "VEI_MOTORISTAPLACA_HORAFIM", nullable = false)
    private LocalTime horaFim;

    @Column(name = "VEI_MOTORISTAPLACA_DTGRAV", updatable = false)
    private LocalDate dataGravacao = LocalDate.now();

    public MotoristaPlacaId(String usuarioCpf, String veiculoPlaca, String diaSemana, 
                            String horaInicio, String horaFim) {
        this.usuario = new Usuario();
        this.usuario.setCpf(usuarioCpf);
        this.veiculo = new Veiculo();
        this.veiculo.setPlaca(veiculoPlaca);
        this.diaSemana = diaSemana;
        this.horaInicio = LocalTime.parse(horaInicio);
        this.horaFim = LocalTime.parse(horaFim);
    }
}