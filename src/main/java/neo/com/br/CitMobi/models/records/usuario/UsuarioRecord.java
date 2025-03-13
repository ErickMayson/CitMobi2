package neo.com.br.CitMobi.models.records.usuario;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public record UsuarioRecord(
        String login,
        String senha,
        String email,
        String nome,
        String telefone,
        UsuarioRole role,
        Operador operador
){

    public Usuario toUsuario(UsuarioRecord record) {
        return new Usuario(record);
    }

    public UsuarioRecord toRecord(Usuario usuario) {
        return new UsuarioRecord(
                usuario.getLogin(),
                usuario.getSenha(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getOperador());
    }

}
