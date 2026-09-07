package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.LinhaPlaca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinhaPlacaRepository extends JpaRepository<LinhaPlaca, Long> {
    List<LinhaPlaca> findByLinha_Id(Long linhaId);
    List<LinhaPlaca> findByVeiculo_Id(Long veiculoId);
    List<LinhaPlaca> findByVeiculo_Placa(String placa);
    List<LinhaPlaca> findByDiaSemana(String diaSemana);
}

