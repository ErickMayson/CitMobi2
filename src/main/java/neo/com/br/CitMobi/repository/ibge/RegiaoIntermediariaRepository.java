package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.RegiaoIntermediaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoIntermediariaRepository extends JpaRepository<RegiaoIntermediaria, Long> {
}
