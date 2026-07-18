package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.UF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UFRepository extends JpaRepository<UF, Long> {
}
