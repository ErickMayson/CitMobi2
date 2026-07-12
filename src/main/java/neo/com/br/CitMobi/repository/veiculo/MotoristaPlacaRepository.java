package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.MotoristaPlaca;
import neo.com.br.CitMobi.models.veiculo.MotoristaPlacaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotoristaPlacaRepository extends JpaRepository<MotoristaPlaca, MotoristaPlacaId> {
    List<MotoristaPlaca> findByMotoristaPlacaIdUsuarioCpf(String cpf);
    List<MotoristaPlaca> findByMotoristaPlacaIdVeiculoPlaca(String placa);
    List<MotoristaPlaca> findByMotoristaPlacaIdDiaSemana(String diaSemana);
}
