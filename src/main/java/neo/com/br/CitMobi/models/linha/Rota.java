package neo.com.br.CitMobi.models.linha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_LIN_ROTA")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_ROTA_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIN_LINHA_ID", nullable = false)
    @JsonIgnore
    private Linha linha;

    @NotBlank
    @Column(name = "LIN_ROTA_PREFIXO", nullable = false)
    private String prefixo;

    @NotBlank
    @Column(name = "LIN_ROTA_SENTIDO", nullable = false, length = 12)
    private String sentido;

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequencia ASC")
    private List<Itinerario> itinerarios = new ArrayList<>();

    public Rota(Linha linha, String prefixo, String sentido) {
        this.linha = linha;
        this.prefixo = prefixo;
        this.sentido = sentido;
    }

    public Rota(Linha linha, String prefixo, String sentido, List<Itinerario> itinerarios) {
        this.linha = linha;
        this.prefixo = prefixo;
        this.sentido = sentido;
        this.itinerarios = itinerarios != null ? itinerarios : new ArrayList<>();
    }
}
