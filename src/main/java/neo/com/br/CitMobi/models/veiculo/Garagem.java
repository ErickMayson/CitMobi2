package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.linha.Operador;

@Data
@Entity
@Table(name = "T_VEI_GARAGEM")
@AllArgsConstructor
@NoArgsConstructor
public class Garagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_GARAGEM_ID")
    private Long garagemId;

    @Column(name = "VEI_GARAGEM_DESCRICAO", nullable = false, length = 155)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_OPERADOR_CNPJ", nullable = false)
    private Operador operador;

    @Column(name = "GLB_MUNICIPIO_COD", nullable = false)
    private Long municipio;

    @Column(name = "VEI_GARAGEM_LOGRADOURO", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "VEI_GARAGEM_NUMERO", nullable = false, length = 24)
    private String numero;

    @Column(name = "VEI_GARAGEM_CEP", nullable = false, length = 8)
    private String cep;

    public Garagem(String descricao, Operador operador, Long municipio, String logradouro, String numero, String cep) {
        this.descricao = descricao;
        this.operador = operador;
        this.municipio = municipio;
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
    }
}