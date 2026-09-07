package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "T_VEI_MOTORISTAPLACA")
@AllArgsConstructor
@NoArgsConstructor
public class MotoristaPlaca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_MOTORISTAPLACA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_USUARIO_ID", nullable = false)
    private Usuario motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_VEICULO_ID", nullable = false)
    private Veiculo veiculo;

    @Column(name = "VEI_MOTORISTAPLACA_DIASEMANA", nullable = false, length = 30)
    private String diaSemana;

    @Column(name = "VEI_MOTORISTAPLACA_HORAINICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "VEI_MOTORISTAPLACA_HORAFIM", nullable = false)
    private LocalTime horaFim;

    @Column(name = "VEI_MOTORISTAPLACA_DTGRAV")
    private LocalDate dataGravacao = LocalDate.now();

    public MotoristaPlaca(Usuario motorista, Veiculo veiculo, String diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.motorista = motorista;
        this.veiculo = veiculo;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataGravacao = LocalDate.now();
    }
}