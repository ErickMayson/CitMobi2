package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito, Long> {
}
