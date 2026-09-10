package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, java.util.UUID> {


    String verifyBy = """
            SELECT
                CASE
                    WHEN USU.USU_USUARIO_LOGIN = :login THEN 'ESSE USUARIO JA ESTA EM USO'
                    WHEN USU.USU_USUARIO_EMAIL = :email THEN 'EMAIL JA ESTA EM USO'
                    WHEN USU.USU_USUARIO_TELEFONE = :telefone THEN 'ESSE TELEFONE JA ESTA EM USO'
                    WHEN USU.USU_USUARIO_CPF = :cpf THEN 'ESSE CPF JA ESTA EM USO'
                END AS FOUND_CONDITION
            FROM T_USU_USUARIO USU
            WHERE USU.USU_USUARIO_LOGIN = :login
               OR USU.USU_USUARIO_EMAIL = :email
               OR USU.USU_USUARIO_TELEFONE = :telefone
               OR USU.USU_USUARIO_CPF = :cpf
            """;


    @Query("SELECT u FROM Usuario u WHERE LOWER(u.login) = LOWER(:login)")
    UserDetails findByLogin(@Param("login") String login);

    @Query(value = "SELECT b FROM Usuario b where b.login = :login AND b.operador.cnpj = :cnpj")
    Optional<Usuario> findUserByLogin(String login, String cnpj);

    Optional<Usuario> findByOperadorCnpjAndLoginAndFlagAtivo(String cnpj, String login,  String flagAtivo);
    List<Usuario> findByOperadorCnpjAndFlagAtivo(String cnpj, String flagAtivo);
    List<Usuario> findByOperadorCnpjAndRoleAndFlagAtivo(String cnpj, UsuarioRole role, String flagAtivo);

    @Query(value =  verifyBy, nativeQuery = true)
    Optional<String> getUsuario(@Param("login") String login, @Param("email") String email, @Param("telefone") String telefone, @Param("cpf") String cpf);
}
