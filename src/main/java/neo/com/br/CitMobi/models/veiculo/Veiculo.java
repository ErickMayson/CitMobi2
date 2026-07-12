package neo.com.br.CitMobi.models.veiculo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.com.br.CitMobi.models.linha.Operador;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "T_VEI_VEICULO")
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {

    @Id
    @Column(name = "VEI_VEICULO_PLACA", length = 16)
    private String placa;

    @Column(name = "VEI_VEICULO_ID", nullable = false, length = 100)
    private String veiculoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_OPERADOR_CNPJ", nullable = false)
    private Operador operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_VEICULOMODELO_ID", nullable = false)
    private VeiculoModelo veiculoModelo;

    @Column(name = "VEI_VEICULO_CAPACIDADE", nullable = false)
    private Integer capacidade;

    @Column(name = "VEI_VEICULO_ANOFABRICACAO", nullable = false, length = 4)
    private String anoFabricacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEI_GARAGEM_ID", nullable = false)
    private Garagem garagem;

    @Column(name = "VEI_VEICULO_FLAGATIVO", nullable = false, length = 1)
    private String flagAtivo = "S";

    @Column(name = "VEI_VEICULO_DTCADASTRO", updatable = false)
    private LocalDate dataCadastro = LocalDate.now();

    public Veiculo(String placa, String veiculoId, Operador operador, VeiculoModelo veiculoModelo, 
                   Integer capacidade, String anoFabricacao, Garagem garagem) {
        this.placa = placa;
        this.veiculoId = veiculoId;
        this.operador = operador;
        this.veiculoModelo = veiculoModelo;
        this.capacidade = capacidade;
        this.anoFabricacao = anoFabricacao;
        this.garagem = garagem;
    }
}