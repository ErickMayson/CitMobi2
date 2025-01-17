package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;

// Criar uma unica linha e colocar itinerarios de ida e volta na tabela itinerario // Isso nem faz sentido
@Entity
@Table(name = "T_LIN_LINHA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Linha {

    @EmbeddedId
    private LinhaId linhaId;

    @NotNull
    @Column(name = "LIN_LINHA_DESCRICAO")
    private String linhaDescricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GLB_OPERADOR_CNPJ", insertable = false, updatable = false)
    private Operador operador;

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGINTERMUNICIPAL")
    private String flagIntermunicipal; // CRIAR ENUM

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGATENDEMETRO")
    private String flagMetro; // CRIAR ENUM

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGATENDETREM")
    private String flagTrem; // CRIAR ENUM

    @Column(name = "LIN_LINHA_FLAGATIVA")
    private String flagAtiva; // FAZER ENUM A(TIVA) OU I(NATIVA) // TALVEZ SEJA MELHOR PADRONIZAR PARA S OU N.

    public Linha(String linhaId,
                 String linhaAtendimento,
                 Long municipio,
                 String linhaDescricao, Operador operador, String flagIntermunicipal, String flagMetro, String flagTrem) {
        this.linhaId = new LinhaId(linhaId, linhaAtendimento, municipio);
        this.flagTrem = flagTrem;
        this.flagMetro = flagMetro;
        this.flagIntermunicipal = flagIntermunicipal;
        this.operador = operador;
        this.linhaDescricao = linhaDescricao;
    }


    public LinhaRecord toRecord() {
        return new LinhaRecord(
                linhaId.getLinhaId(),
                linhaId.getLinhaAtendimento(),
                linhaId.getMunicipio(),
                linhaDescricao,
                new OperadorRecord(operador.getCnpj(), operador.getRazaoSocial()),
                flagIntermunicipal,
                flagMetro,
                flagTrem
        );
    }

}
