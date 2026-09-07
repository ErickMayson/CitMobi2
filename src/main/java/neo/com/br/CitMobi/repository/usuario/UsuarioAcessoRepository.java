package neo.com.br.CitMobi.repository.usuario;

import neo.com.br.CitMobi.models.usuario.UsuarioAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioAcessoRepository extends JpaRepository<UsuarioAcesso, Long> {
    List<UsuarioAcesso> findByUsuario_Id(UUID usuarioId);
}
