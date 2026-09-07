package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_LIN_ITINERARIO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_ITINERARIO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_ROTA_ID", nullable = false)
    private Rota rota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_PARADA_ID", nullable = false)
    private Parada parada;

    @Column(name = "LIN_ITINERARIO_SEQUENCIA", nullable = false)
    private Integer sequencia;

    public Itinerario(Rota rota, Parada parada, Integer sequencia) {
        this.rota = rota;
        this.parada = parada;
        this.sequencia = sequencia;
    }
}

