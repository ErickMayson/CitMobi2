package neo.com.br.CitMobi.repository.telemetria;

import neo.com.br.CitMobi.models.telemetria.Telemetria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria, Long> {
    Optional<Telemetria> findByVeiculoId(Long veiculoId);
    List<Telemetria> findByViagem_Id(Long viagemId);
}
