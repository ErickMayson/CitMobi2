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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VEI_VEICULO_ID")
    private Long id;

    @Column(name = "VEI_VEICULO_PLACA", unique = true, nullable = false, length = 16)
    private String placa;

    @Column(name = "VEI_VEICULO_CODIGO", nullable = false, length = 100)
    private String codigoVeiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_OPERADOR_ID", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "VEI_VEICULO_STATUS", nullable = false, length = 20)
    private VeiculoStatus status = VeiculoStatus.ATIVO;

    @Column(name = "VEI_VEICULO_DTCADASTRO", updatable = false)
    private LocalDate dataCadastro = LocalDate.now();

    public Veiculo(String placa, String codigoVeiculo, Operador operador, VeiculoModelo veiculoModelo, 
                   Integer capacidade, String anoFabricacao, Garagem garagem) {
        this.placa = placa;
        this.codigoVeiculo = codigoVeiculo;
        this.operador = operador;
        this.veiculoModelo = veiculoModelo;
        this.capacidade = capacidade;
        this.anoFabricacao = anoFabricacao;
        this.garagem = garagem;
        this.status = VeiculoStatus.ATIVO;
        this.dataCadastro = LocalDate.now();
    }
}