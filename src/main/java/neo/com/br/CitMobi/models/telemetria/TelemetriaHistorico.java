package neo.com.br.CitMobi.models.telemetria;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.viagem.Viagem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "T_VEI_TELEMETRIA_HISTORICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TelemetriaHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_TELEMETRIAHIST_ID")
    private Long id;

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

    @Column(name = "VEI_TELEMETRIA_DTREGISTRO", nullable = false)
    private OffsetDateTime dataRegistro = OffsetDateTime.now();
}
