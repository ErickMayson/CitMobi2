package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.VeiculoEscala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VeiculoEscalaRepository extends JpaRepository<VeiculoEscala, Long> {
    List<VeiculoEscala> findByVeiculo_Id(Long veiculoId);
    List<VeiculoEscala> findByVeiculo_Placa(String placa);
    List<VeiculoEscala> findByLinha_Id(Long linhaId);
    List<VeiculoEscala> findByMotorista_Id(UUID motoristaId);
    List<VeiculoEscala> findByMotorista_Cpf(String cpf);
    List<VeiculoEscala> findByDiaSemana(String diaSemana);
}


