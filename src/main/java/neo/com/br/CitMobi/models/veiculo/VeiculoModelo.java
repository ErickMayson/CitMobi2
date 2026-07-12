package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "T_VEI_VEICULOMODELO")
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_VEICULOMODELO_ID")
    private Long veiculoModeloId;

    @Column(name = "VEI_VEICULO_MARCA", nullable = false, length = 256)
    private String marca;

    @Column(name = "VEI_VEICULO_MODELO", nullable = false, length = 256)
    private String modelo;

    @Column(name = "VEI_VEICULO_EIXO", nullable = false)
    private Integer eixo;

    @Column(name = "VEI_VEICULO_TIPO", nullable = false, length = 128)
    private String tipo;

    public VeiculoModelo(String marca, String modelo, Integer eixo, String tipo) {
        this.marca = marca;
        this.modelo = modelo;
        this.eixo = eixo;
        this.tipo = tipo;
    }
}