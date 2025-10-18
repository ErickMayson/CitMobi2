package neo.com.br.CitMobi.models.records.usuario;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;

public record UsuarioLiteRecord(
        String login,
        String email,
        String nome,
        String telefone,
        String cpf,
        UsuarioRole role,
        Operador operador
) {
    // Convert from entity
    public static UsuarioLiteRecord fromUsuario(Usuario usuario) {
        return new UsuarioLiteRecord(
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getTelefone(),
                usuario.getCpf(),
                usuario.getRole(),
                usuario.getOperador()
        );
    }

    // Optionally convert back to entity if needed
    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setTelefone(telefone);
        usuario.setCpf(cpf);
        usuario.setRole(role);
        usuario.setOperador(operador);
        return usuario;
    }
}
