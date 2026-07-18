package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoRepository extends JpaRepository<Regiao, Long> {
}
