package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.LinhaPlaca;
import neo.com.br.CitMobi.models.veiculo.LinhaPlacaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinhaPlacaRepository extends JpaRepository<LinhaPlaca, LinhaPlacaId> {
    List<LinhaPlaca> findByLinhaPlacaIdLinhaIdAndLinhaPlacaIdLinhaAtendimentoAndLinhaPlacaIdMunicipio(
        String linhaId, String linhaAtendimento, Long municipio
    );
    List<LinhaPlaca> findByLinhaPlacaIdVeiculoPlaca(String placa);
    List<LinhaPlaca> findByLinhaPlacaIdDiaSemana(String diaSemana);
}
