package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Itinerario;
import neo.com.br.CitMobi.models.linha.ItinerarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItinerarioRepository extends JpaRepository<Itinerario, ItinerarioId> {

    @Query(value = """
            SELECT * FROM T_LIN_ITINERARIO ITI
            WHERE ITI.LIN_ROTA_ID = :rotaId
            """,
            nativeQuery = true)
    Optional<List<Itinerario>> getRotaByItinerarioId(
            @Param("rotaId") Long rotaId
    );

    @Query(value = """
            SELECT * FROM T_LIN_ITINERARIO ITI
            WHERE ITI.LIN_ROTA_ID IN :rotaId
            """,
            nativeQuery = true)
    Optional<List<Itinerario>> getRotasFromItinerario(
            @Param("rotaId") List<Long> rotaId
    );

    @Query(value = """
            SELECT ITI.* FROM T_LIN_ITINERARIO ITI
                        LEFT JOIN T_LIN_ROTA ROTA ON ROTA.LIN_ROTA_ID = ITI.LIN_ROTA_ID
                        WHERE ROTA.LIN_LINHA_ID = :linha
                        AND ROTA.LIN_LINHA_ATENDIMENTO = :atendimento
                        AND ROTA.GLB_MUNICIPIO_COD = :municipio
                        ORDER BY ROTA.LIN_ROTA_SENTIDO ASC, ITI.LIN_ITINERARIO_SEQUENCIA ASC
            """,
            nativeQuery = true)
    Optional<List<Itinerario>> getItinerariosPerLine(
            @Param("linha") String linha,
            @Param("atendimento") String atendimento,
            @Param("municipio") Long municipio
            );

}
