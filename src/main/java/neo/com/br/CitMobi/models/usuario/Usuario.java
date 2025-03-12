package neo.com.br.CitMobi.models.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.com.br.CitMobi.models.linha.Operador;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_USU_USUARIO")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USU_USUARIO_ID")
    private String id;

    @Column(name = "USU_USUARIO_LOGIN", length = 50, nullable = false, unique = true)
    private String login;

    @Column(name = "USU_USUARIO_CNPJ", length = 14, nullable = false)
    private String cnpj;

    @Column(name = "USU_USUARIO_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USU_USUARIO_SENHA", nullable = false)
    private String senha;

    @Column(name = "USU_USUARIO_NOME", length = 100)
    private String nome;

    @Column(name = "USU_USUARIO_TELEFONE", length = 20, unique = true)
    private String telefone;

    @Column(name = "USU_USUARIO_ROLE", nullable = false)
    @Enumerated(EnumType.STRING) // Add this annotation to ensure the enum is stored as a string
    private UsuarioRole role;

    @Column(name = "USU_USUARIO_FLAGATIVO", nullable = false, columnDefinition = "TEXT DEFAULT 'S'")
    private String flagAtivo = "S";

    @Column(name = "USU_USUARIO_DTCRIACAO", updatable = false)
    private Instant dataCriacao = Instant.now();

    @Column(name = "USU_USUARIO_DTUPDATE")
    private Instant dataUpdate;

    @Column(name = "USU_USUARIO_DTULTIMOLOGIN")
    private Instant dataUltimoLogin;

    @ManyToOne
    @JoinColumn(name = "USU_USUARIO_CNPJ", referencedColumnName = "GLB_OPERADOR_CNPJ", insertable = false, updatable = false)
    private Operador operador;

    public Usuario(String login, String senha, UsuarioRole role) {
        this.login = login;
        this.senha = senha;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UsuarioRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

