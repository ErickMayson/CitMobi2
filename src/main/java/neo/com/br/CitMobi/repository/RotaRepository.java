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
            SELECT * FROM T_LIN_ROTA ROTA
            WHERE ROTA.LIN_ITINERARIO_ID = :itinerarioId
            """,
            nativeQuery = true)
    Optional<Rota> getRotaByItinerarioId(
            @Param("itinerarioId") String itinerarioId
    );

    @Query(value = """
            SELECT * FROM T_LIN_ROTA ROTA
            WHERE ROTA.LIN_ITINERARIO_ID IN :itinerarioId
            """,
            nativeQuery = true)
    Optional<List<Rota>> getRotasFromItinerario(
            @Param("itinerarioId") List<String> itinerarioId
    );

}
