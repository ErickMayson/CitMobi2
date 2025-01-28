package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;

import java.util.List;

@Entity
@Table(name = "T_LIN_ITINERARIO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Itinerario {

    @Id
    @NotNull
    @Column(name = "LIN_ITINERARIO_ID")
    private Long itinerarioId;

    @NotBlank
    @Column(name = "LIN_LINHA_ID", nullable = false)
    private String linhaId;

    @NotBlank
    @Column(name = "LIN_LINHA_ATENDIMENTO", nullable = false)
    private String linhaAtendimento;

    @NotBlank
    @Column(name = "LIN_ITINERARIO_PREFIXO")
    private String prefixo;

    @NotNull
    @Column(name = "GLB_MUNICIPIO_COD", nullable = false, updatable = false)
    private Long municipio;

    @NotBlank
    @Column(name = "LIN_ITINERARIO_SENTIDO", nullable = false)
    private String linhaSentido;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rotaId.itinerario")
    private List<Rota> rota;

    public Itinerario(String linhaId, String linhaAtendimento, String prefixo, Long municipio, String linhaSentido, List<Rota> rota) {
        this.linhaId = linhaId;
        this.linhaAtendimento = linhaAtendimento;
        this.prefixo = prefixo;
        this.municipio = municipio;
        this.linhaSentido = linhaSentido;
        this.rota = rota;
    }
}
