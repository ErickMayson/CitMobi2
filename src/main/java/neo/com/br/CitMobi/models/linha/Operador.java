package neo.com.br.CitMobi.models.linha;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_GLB_OPERADOR")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GLB_OPERADOR_ID")
    private Long id;

    @Column(name = "GLB_OPERADOR_CNPJ", unique = true, nullable = false, length = 14)
    private String cnpj;

    @Column(name = "GLB_OPERADOR_RAZAOSOCIAL", nullable = false)
    private String razaoSocial;

    @Column(name = "GLB_OPERADOR_FLAGREGULADOR", nullable = false, length = 1)
    private String flagRegulador = "S";

    public Operador(String cnpj) {
        this.cnpj = cnpj;
    }

    public Operador(String cnpj, String razaoSocial) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }

    public Operador(String cnpj, String razaoSocial, String flagRegulador) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.flagRegulador = flagRegulador;
    }
}

