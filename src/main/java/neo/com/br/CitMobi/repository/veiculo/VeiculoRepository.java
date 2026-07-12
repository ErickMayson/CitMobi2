package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
    List<Veiculo> findByOperadorCnpj(String cnpj);
    List<Veiculo> findByGaragemGaragemId(Long garagemId);
    List<Veiculo> findByFlagAtivo(String flagAtivo);
    List<Veiculo> findByVeiculoModeloVeiculoModeloId(Long modeloId);
}
