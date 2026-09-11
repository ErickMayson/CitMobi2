package neo.com.br.CitMobi.repository.telemetria;

import neo.com.br.CitMobi.models.telemetria.TelemetriaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetriaHistoricoRepository extends JpaRepository<TelemetriaHistorico, Long> {
    List<TelemetriaHistorico> findByViagem_IdOrderByDataRegistroDesc(Long viagemId);
    List<TelemetriaHistorico> findByViagem_IdOrderByDataRegistroAsc(Long viagemId);
}
