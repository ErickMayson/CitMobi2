package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
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

    @NotBlank
    @Column(name = "GLB_OPERADOR_CNPJOPERADOR")
    private String cnpjOperador;

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGINTERMUNICIPAL")
    private String flagIntermunicipal;

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGATENDEMETRO")
    private String flagMetro;

    @NotBlank
    @Column(name = "LIN_LINHA_FLAGATENDETREM")
    private String flagTrem;

    public Linha(String linhaId,
                 String linhaAtendimento,
                 Long municipio,
                 String linhaDescricao, String cnpjOperador, String flagIntermunicipal,String flagMetro, String flagTrem) {
        this.linhaId = new LinhaId(linhaId, linhaAtendimento, municipio);
        this.flagTrem = flagTrem;
        this.flagMetro = flagMetro;
        this.flagIntermunicipal = flagIntermunicipal;
        this.cnpjOperador = cnpjOperador;
        this.linhaDescricao = linhaDescricao;
    }


    public LinhaRecord toRecord() {
        return new LinhaRecord(
                linhaId.getLinhaId(),
                linhaId.getLinhaAtendimento(),
                linhaId.getMunicipio(),
                linhaDescricao,
                cnpjOperador,
                flagIntermunicipal,
                flagMetro,
                flagTrem
        );
    }

}
