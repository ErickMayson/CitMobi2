package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
public class LinhaPlacaId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(name = "LIN_LINHA_ID", length = 30)
    private String linhaId;

    @NotBlank
    @Column(name = "LIN_LINHA_ATENDIMENTO", length = 15)
    private String linhaAtendimento;

    @NotNull
    @Column(name = "GLB_MUNICIPIO_COD")
    private Long municipio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VEI_VEICULO_PLACA")
    private Veiculo veiculo;

    @NotBlank
    @Column(name = "VEI_LINHAPLACA_DIASEMANA", length = 30)
    private String diaSemana;

    @Column(name = "VEI_LINHAPLACA_HORAINICIO")
    private LocalTime horaInicio;

    @Column(name = "VEI_LINHAPLACA_HORAFIM")
    private LocalTime horaFim;

    @Column(name = "VEI_LINHAPLACA_DTGRAV", updatable = false)
    private LocalDate dataGravacao = LocalDate.now();

    public LinhaPlacaId(String linhaId, String linhaAtendimento, Long municipio, 
                        String veiculoPlaca, String diaSemana, String horaInicio, String horaFim) {
        this.linhaId = linhaId;
        this.linhaAtendimento = linhaAtendimento;
        this.municipio = municipio;
        this.veiculo = new Veiculo();
        this.veiculo.setPlaca(veiculoPlaca);
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio != null ? LocalTime.parse(horaInicio) : null;
        this.horaFim = horaFim != null ? LocalTime.parse(horaFim) : null;
    }
}