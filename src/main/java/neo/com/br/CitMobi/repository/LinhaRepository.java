package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinhaRepository extends JpaRepository<Linha, LinhaId> {
}
