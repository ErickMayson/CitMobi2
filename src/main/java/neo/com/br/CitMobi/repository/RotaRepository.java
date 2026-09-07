package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {

    List<Rota> findByLinha_Id(Long linhaId);

    Optional<Rota> findByLinha_IdAndSentido(Long linhaId, String sentido);

    boolean existsByLinha_IdAndSentido(Long linhaId, String sentido);
}

