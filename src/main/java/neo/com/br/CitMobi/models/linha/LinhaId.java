package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.ibge.UF;

@Embeddable
public class LinhaId {

    @Column(name = "LIN_LINHA_ID")
    private String linhaId;

    @Column(name="LIN_LINHA_ATENDIMENTO")
    private String linhaAtendimento;

    @ManyToOne
    @JoinColumn(name = "GLB_MUNICIPIO_COD", nullable = false)
    private Municipio municipio;

    @Column(name = "LIN_LINHA_SENTIDO")
    private String linhaSentido;

}
