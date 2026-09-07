package neo.com.br.CitMobi.models.viagem;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.veiculo.Veiculo;

import java.time.OffsetDateTime;

@Entity
@Table(name = "T_USU_VIAGEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USU_VIAGEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_USUARIO_ID", nullable = false)
    private Usuario motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_VEICULO_ID", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_LINHA_ID", nullable = false)
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_ROTA_ID", nullable = false)
    private Rota rota;

    @Column(name = "USU_VIAGEM_DTINICIO", nullable = false)
    private OffsetDateTime dataInicio = OffsetDateTime.now();

    @Column(name = "USU_VIAGEM_DTFIM")
    private OffsetDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "USU_VIAGEM_STATUS", nullable = false, length = 20)
    private ViagemStatus status = ViagemStatus.EM_ANDAMENTO;

    public Viagem(Usuario motorista, Veiculo veiculo, Linha linha, Rota rota) {
        this.motorista = motorista;
        this.veiculo = veiculo;
        this.linha = linha;
        this.rota = rota;
        this.dataInicio = OffsetDateTime.now();
        this.status = ViagemStatus.EM_ANDAMENTO;
    }
}
