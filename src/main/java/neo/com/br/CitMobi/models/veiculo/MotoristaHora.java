package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.usuario.Usuario;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "T_USU_MOTORISTAHORA")
@AllArgsConstructor
@NoArgsConstructor
public class MotoristaHora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USU_MOTORISTAHORA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_USUARIO_ID", nullable = false)
    private Usuario motorista;

    @Column(name = "USU_MOTORISTAHORA_DIASEMANA", nullable = false, length = 30)
    private String diaSemana;

    @Column(name = "USU_MOTORISTAHORA_HORAINICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "USU_MOTORISTAHORA_HORAFIM", nullable = false)
    private LocalTime horaFim;

    @Column(name = "USU_MOTORISTAHORA_PAUSAINICIO", nullable = false)
    private LocalTime pausaInicio;

    @Column(name = "USU_MOTORISTAHORA_PAUSAFIM", nullable = false)
    private LocalTime pausaFim;

    public MotoristaHora(Usuario motorista, String diaSemana, LocalTime horaInicio, 
                         LocalTime horaFim, LocalTime pausaInicio, LocalTime pausaFim) {
        this.motorista = motorista;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.pausaInicio = pausaInicio;
        this.pausaFim = pausaFim;
    }
}