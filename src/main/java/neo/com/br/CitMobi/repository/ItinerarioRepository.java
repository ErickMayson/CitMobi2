package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Itinerario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    @Query(value = """
            SELECT * FROM T_LIN_ITINERARIO ITI
            WHERE ITI.LIN_LINHA_ID = :linhaId
            AND ITI.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND ITI.GLB_MUNICIPIO_COD = :municipio
            """,
            nativeQuery = true)
    Optional<List<Itinerario>> getItinerarioIdByLinhaId(
            @Param("linhaId") String linha,
            @Param("linhaAtendimento") String atendimento,
            @Param("municipio") Long municipio
    );

}
