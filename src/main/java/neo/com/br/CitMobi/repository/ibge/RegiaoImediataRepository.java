package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.RegiaoImediata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoImediataRepository extends JpaRepository<RegiaoImediata, Long> {
}
