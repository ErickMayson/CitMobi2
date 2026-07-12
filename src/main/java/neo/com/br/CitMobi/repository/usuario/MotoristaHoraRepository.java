package neo.com.br.CitMobi.repository.usuario;

import neo.com.br.CitMobi.models.veiculo.MotoristaHora;
import neo.com.br.CitMobi.models.veiculo.MotoristaHoraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotoristaHoraRepository extends JpaRepository<MotoristaHora, MotoristaHoraId> {
    List<MotoristaHora> findByMotoristaHoraIdUsuarioCpf(String cpf);
    List<MotoristaHora> findByMotoristaHoraIdDiaSemana(String diaSemana);
}
