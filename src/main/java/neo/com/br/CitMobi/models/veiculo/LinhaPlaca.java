package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.linha.Linha;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "T_VEI_LINHAPLACA")
@AllArgsConstructor
@NoArgsConstructor
public class LinhaPlaca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_LINHAPLACA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_LINHA_ID", nullable = false)
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_VEICULO_ID", nullable = false)
    private Veiculo veiculo;

    @Column(name = "VEI_LINHAPLACA_DIASEMANA", nullable = false, length = 30)
    private String diaSemana;

    @Column(name = "VEI_LINHAPLACA_HORAINICIO")
    private LocalTime horaInicio;

    @Column(name = "VEI_LINHAPLACA_HORAFIM")
    private LocalTime horaFim;

    @Column(name = "VEI_LINHAPLACA_DTGRAV")
    private LocalDate dataGravacao = LocalDate.now();

    public LinhaPlaca(Linha linha, Veiculo veiculo, String diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.linha = linha;
        this.veiculo = veiculo;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataGravacao = LocalDate.now();
    }
}