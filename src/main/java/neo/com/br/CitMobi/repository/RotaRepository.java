package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RotaRepository extends JpaRepository<Rota, RotaId> {

    @Query(value = """
            SELECT * FROM T_LIN_ITINERARIO ITI
            WHERE ITI.LIN_ITINERARIO_ID = :itinerarioId
            """,
            nativeQuery = true)
    Optional<List<Rota>> getRotaByItinerarioId(
            @Param("itinerarioId") String itinerarioId
    );

}
