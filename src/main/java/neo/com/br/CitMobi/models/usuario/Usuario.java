package neo.com.br.CitMobi.models.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.usuario.UsuarioRecord;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    private UUID id;

    @Column(name = "USU_USUARIO_LOGIN", length = 50, nullable = false, unique = true)
    private String login;

    @Column(name = "USU_USUARIO_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USU_USUARIO_SENHA", nullable = false)
    private String senha;

    @Column(name = "USU_USUARIO_NOME", length = 100)
    private String nome;

    @Column(name = "USU_USUARIO_CPF", length = 11)
    private String cpf;

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
    @JoinColumn(name = "GLB_OPERADOR_CNPJ", referencedColumnName = "GLB_OPERADOR_CNPJ")
    private Operador operador;

    public Usuario(String login, String senha, UsuarioRole role) {
        this.login = login;
        this.senha = senha;
        this.role = role;
    }

    public Usuario(UsuarioRecord usuario) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.login = usuario.login();
        this.senha = encoder.encode(usuario.senha());
        this.email = usuario.email();
        this.cpf = usuario.cpf();
        this.telefone = usuario.telefone();
        this.nome = usuario.nome();
        this.role = usuario.role();
        if (usuario.operador() != null) {
            this.operador = new Operador(usuario.operador().getCnpj(), usuario.operador().getRazaoSocial());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UsuarioRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        } else if (this.role == UsuarioRole.MOTORISTA) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_MOTORISTA")
            );
        } else {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }
    }


    @Override
    public String getPassword() {
        return this.senha;
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

