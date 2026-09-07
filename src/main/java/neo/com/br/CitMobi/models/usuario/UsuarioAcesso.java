package neo.com.br.CitMobi.models.usuario;

import jakarta.persistence.*;
import lombok.*;
import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.ibge.UF;

@Entity
@Table(name = "T_USU_USUARIO_ACESSO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USU_ACESSO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USU_USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_MUNICIPIO_COD")
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GLB_UF_COD")
    private UF uf;

    public UsuarioAcesso(Usuario usuario, Municipio municipio, UF uf) {
        this.usuario = usuario;
        this.municipio = municipio;
        this.uf = uf;
    }
}
