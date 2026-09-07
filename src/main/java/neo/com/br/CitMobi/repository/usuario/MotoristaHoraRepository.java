package neo.com.br.CitMobi.repository.usuario;

import neo.com.br.CitMobi.models.veiculo.MotoristaHora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MotoristaHoraRepository extends JpaRepository<MotoristaHora, Long> {
    List<MotoristaHora> findByMotorista_Id(UUID motoristaId);
    List<MotoristaHora> findByMotorista_Cpf(String cpf);
    List<MotoristaHora> findByDiaSemana(String diaSemana);
}

