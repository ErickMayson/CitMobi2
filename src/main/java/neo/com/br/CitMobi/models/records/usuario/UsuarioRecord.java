package neo.com.br.CitMobi.models.records.usuario;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;

public record UsuarioRecord(
        String login,
        String senha,
        String email,
        String nome,
        String telefone,
        String cpf,
        String flagAtivo,
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
                usuario.getCpf(),
                usuario.getFlagAtivo(),
                usuario.getRole(),
                usuario.getOperador());
    }

}
