package neo.com.br.CitMobi.models.telemetria;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.viagem.Viagem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "T_VEI_TELEMETRIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Telemetria {

    @Id
    @Column(name = "VEI_VEICULO_ID")
    private Long veiculoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "VEI_VEICULO_ID")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_VIAGEM_ID", nullable = false)
    private Viagem viagem;

    @Column(name = "VEI_TELEMETRIA_LATITUDE", nullable = false)
    private BigDecimal latitude;

    @Column(name = "VEI_TELEMETRIA_LONGITUDE", nullable = false)
    private BigDecimal longitude;

    @Column(name = "VEI_TELEMETRIA_VELOCIDADE")
    private BigDecimal velocidade;

    @Column(name = "VEI_TELEMETRIA_BEARING")
    private BigDecimal bearing;

    @Column(name = "VEI_TELEMETRIA_ODOMETER")
    private BigDecimal odometer;

    @Column(name = "VEI_TELEMETRIA_SEQPARADA")
    private Integer sequenciaParadaAtual;

    @Column(name = "VEI_TELEMETRIA_STATUSPARADA", length = 30)
    private String statusParadaAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_PARADA_ID")
    private Parada paradaAtual;

    @Column(name = "VEI_TELEMETRIA_DTUPDATE", nullable = false)
    private OffsetDateTime ultimaAtualizacao = OffsetDateTime.now();
}
