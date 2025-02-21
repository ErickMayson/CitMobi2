package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RotaRepository extends JpaRepository<Rota, Long> {

    @Query(value = """
            SELECT * FROM T_LIN_ROTA ROTA
            WHERE ROTA.LIN_LINHA_ID = :linhaId
            AND ROTA.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND ROTA.GLB_MUNICIPIO_COD = :municipio
            """,
            nativeQuery = true)
    Optional<List<Rota>> getRotaIdByLinhaId(
            @Param("linhaId") String linha,
            @Param("linhaAtendimento") String atendimento,
            @Param("municipio") Long municipio
    );

    @Query(value = """
            SELECT CASE
                WHEN EXISTS (
                    SELECT 1 FROM T_LIN_ROTA ROTA
                    WHERE ROTA.LIN_LINHA_ID = :linhaId
                    AND ROTA.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
                    AND ROTA.GLB_MUNICIPIO_COD = :municipio
                    AND ROTA.LIN_ROTA_SENTIDO = :sentido
                    ) THEN TRUE ELSE FALSE
            END
            """,
            nativeQuery = true)
    boolean doesRotaExists(
            @Param("linhaId") String linha,
            @Param("linhaAtendimento") String atendimento,
            @Param("municipio") Long municipio,
            @Param("sentido") String sentido
    );

}
