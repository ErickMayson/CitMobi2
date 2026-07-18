package neo.com.br.CitMobi.repository.ibge;

import neo.com.br.CitMobi.models.ibge.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
}
