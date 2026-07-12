package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.VeiculoModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoModeloRepository extends JpaRepository<VeiculoModelo, Long> {
    List<VeiculoModelo> findByMarca(String marca);
    List<VeiculoModelo> findByTipo(String tipo);
}
