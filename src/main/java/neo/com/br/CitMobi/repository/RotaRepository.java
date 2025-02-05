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
    Optional<List<Rota>> getRotaByItinerarioId(
            @Param("itinerarioId") Long itinerarioId
    );

    @Query(value = """
            SELECT * FROM T_LIN_ROTA ROTA
            WHERE ROTA.LIN_ITINERARIO_ID IN :itinerarioId
            """,
            nativeQuery = true)
    Optional<List<Rota>> getRotasFromItinerario(
            @Param("itinerarioId") List<Long> itinerarioId
    );

    @Query(value = """
            SELECT ROTA.* FROM T_LIN_ROTA ROTA
            LEFT JOIN T_LIN_ITINERARIO ITI ON ITI.LIN_ITINERARIO_ID = ROTA.LIN_ITINERARIO_ID
            WHERE ITI.LIN_LINHA_ID = :linhaId
            AND ITI.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND ITI.GLB_MUNICIPIO_COD = :municipio
            ORDER BY ITI.LIN_ITINERARIO_SENTIDO ASC, ROTA.LIN_ROTA_SEQUENCIA ASC
            """,
            nativeQuery = true)
    Optional<List<Rota>> getRotasPerLine(
            @Param("linhaId") String linha,
            @Param("linhaAtendimento") String atendimento,
            @Param("municipio") Long municipio
            );

}
