package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "T_LIN_ROTA")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for SERIAL in PostgreSQL
    @Column(name = "LIN_ROTA_ID")
    private Long rotaId;

    @NotBlank
    @Column(name = "LIN_LINHA_ID", nullable = false)
    private String linhaId;

    @NotBlank
    @Column(name = "LIN_LINHA_ATENDIMENTO", nullable = false)
    private String linhaAtendimento;

    @NotBlank
    @Column(name = "LIN_ROTA_PREFIXO")
    private String prefixo;

    @NotNull
    @Column(name = "GLB_MUNICIPIO_COD", nullable = false, updatable = false)
    private Long municipio;

    @NotBlank
    @Column(name = "LIN_ROTA_SENTIDO", nullable = false)
    private String linhaSentido;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "itinerarioId.rotaId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Itinerario> itinerario;

    public Rota(String linhaId, String linhaAtendimento, String prefixo, Long municipio, String linhaSentido, List<Itinerario> itinerario) {
        this.linhaId = linhaId;
        this.linhaAtendimento = linhaAtendimento;
        this.prefixo = prefixo;
        this.municipio = municipio;
        this.linhaSentido = linhaSentido;
        this.itinerario = itinerario;
    }

    public Rota(String linhaId, String linhaAtendimento, String prefixo, Long municipio, String linhaSentido) {
        this.linhaId = linhaId;
        this.linhaAtendimento = linhaAtendimento;
        this.prefixo = prefixo;
        this.municipio = municipio;
        this.linhaSentido = linhaSentido;
    }

}
