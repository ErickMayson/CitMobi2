package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// Criar uma unica linha e colocar itinerarios de ida e volta na tabela itinerario // Isso nem faz sentido
@Entity
@Table(name = "T_LIN_LINHA")
public class Linha {

    @EmbeddedId
    private LinhaId linhaId;

    @Column(name = "LIN_LINHA_DESCRICAO")
    private String linhaDescricao;

    @Column(name = "GLB_OPERADOR_CNPJOPERADOR")
    private String cnpjOperador;

    @Column(name = "LIN_LINHA_FLAGINTERMUNICIPAL")
    private String flagIntermunicipal;

    @Column(name = "LIN_LINHA_FLAGATENDEMETRO")
    private String flagMetro;

    @Column(name = "LIN_LINHA_FLAGATENDETREM")
    private String flagTrem;

}
