package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.MotoristaPlaca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MotoristaPlacaRepository extends JpaRepository<MotoristaPlaca, Long> {
    List<MotoristaPlaca> findByMotorista_Id(UUID motoristaId);
    List<MotoristaPlaca> findByMotorista_Cpf(String cpf);
    List<MotoristaPlaca> findByVeiculo_Id(Long veiculoId);
    List<MotoristaPlaca> findByVeiculo_Placa(String placa);
    List<MotoristaPlaca> findByDiaSemana(String diaSemana);
}

