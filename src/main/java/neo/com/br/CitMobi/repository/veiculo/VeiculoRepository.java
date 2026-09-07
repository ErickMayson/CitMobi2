package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.Veiculo;
import neo.com.br.CitMobi.models.veiculo.VeiculoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {
    Optional<Veiculo> findByPlaca(String placa);
    List<Veiculo> findByOperador_Id(Long operadorId);
    List<Veiculo> findByOperador_Cnpj(String cnpj);
    List<Veiculo> findByGaragem_Id(Long garagemId);
    List<Veiculo> findByStatus(VeiculoStatus status);
    List<Veiculo> findByVeiculoModelo_VeiculoModeloId(Long modeloId);
}

