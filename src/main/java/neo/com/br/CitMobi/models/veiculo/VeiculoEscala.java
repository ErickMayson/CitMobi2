package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "T_VEI_ESCALA")
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoEscala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_ESCALA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_VEICULO_ID", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_LINHA_ID", nullable = false)
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_USUARIO_ID", nullable = true)
    private Usuario motorista;

    @Column(name = "VEI_ESCALA_DIASEMANA", nullable = false, length = 30)
    private String diaSemana;

    @Column(name = "VEI_ESCALA_HORAINICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "VEI_ESCALA_HORAFIM", nullable = false)
    private LocalTime horaFim;

    @Column(name = "VEI_ESCALA_DTGRAV")
    private LocalDate dataGravacao = LocalDate.now();

    public VeiculoEscala(Veiculo veiculo, Linha linha, Usuario motorista, String diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.veiculo = veiculo;
        this.linha = linha;
        this.motorista = motorista;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataGravacao = LocalDate.now();
    }
}