package neo.com.br.CitMobi.models.linha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_LIN_LINHA")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"rotas", "municipio", "operador"})
public class Linha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIN_LINHA_ID")
    private Long id;

    @NotBlank
    @Column(name = "LIN_LINHA_CODIGO", nullable = false, length = 30)
    private String codigoLinha;

    @NotBlank
    @Column(name = "LIN_LINHA_ATENDIMENTO", nullable = false, length = 15)
    private String atendimento;

    @NotNull
    @Column(name = "LIN_LINHA_DESCRICAO", nullable = false)
    private String linhaDescricao;

    public String getDescricao() {
        return linhaDescricao;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_MUNICIPIO_COD", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_OPERADOR_ID", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Operador operador;

    @Column(name = "LIN_LINHA_FLAGINTERMUNICIPAL", nullable = false, length = 1)
    private String flagIntermunicipal = "N";

    @Column(name = "LIN_LINHA_FLAGATENDEMETRO", nullable = false, length = 1)
    private String flagMetro = "N";

    @Column(name = "LIN_LINHA_FLAGATENDETREM", nullable = false, length = 1)
    private String flagTrem = "N";

    @Column(name = "LIN_LINHA_FLAGATIVA", nullable = false, length = 1)
    private String flagAtiva = "S";

    @OneToMany(mappedBy = "linha", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Rota> rotas = new ArrayList<>();

    public Linha(String codigoLinha,
                 String atendimento,
                 Municipio municipio,
                 Operador operador,
                 String linhaDescricao,
                 String flagIntermunicipal,
                 String flagMetro,
                 String flagTrem,
                 String flagAtiva) {
        this.codigoLinha = codigoLinha;
        this.atendimento = atendimento;
        this.municipio = municipio;
        this.operador = operador;
        this.linhaDescricao = linhaDescricao;
        this.flagIntermunicipal = flagIntermunicipal;
        this.flagMetro = flagMetro;
        this.flagTrem = flagTrem;
        this.flagAtiva = flagAtiva;
    }

    public LinhaRecord toRecord() {
        return new LinhaRecord(
                id,
                codigoLinha,
                atendimento,
                municipio != null ? municipio.getCodigoIbge() : null,
                operador != null ? new neo.com.br.CitMobi.models.records.glb.OperadorRecord(operador.getCnpj(), operador.getRazaoSocial()) : null,
                linhaDescricao,
                flagIntermunicipal,
                flagMetro,
                flagTrem,
                flagAtiva
        );
    }
}
