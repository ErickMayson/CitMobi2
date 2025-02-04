package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParadaRepository extends JpaRepository<Parada, Long> {
}
