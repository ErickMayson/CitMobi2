package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import neo.com.br.CitMobi.models.linha.LinhaOperador;
import neo.com.br.CitMobi.models.linha.LinhaOperadorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LinhaOperadorRepository extends JpaRepository<LinhaOperador, LinhaOperadorId> {

    @Query(value = """
            SELECT * FROM T_LIN_LINHA_OPERADOR LIN
            WHERE LIN.LIN_LINHA_ID = :linhaId
            AND LIN.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND LIN.GLB_MUNICIPIO_COD = :municipio
            """,
            nativeQuery = true)
    Optional<List<LinhaOperador>> findByLinhaId(
            @Param("linhaId") String linhaId,
            @Param("linhaAtendimento") String linhaAtendimento,
            @Param("municipio") Long municipio
    );

}
