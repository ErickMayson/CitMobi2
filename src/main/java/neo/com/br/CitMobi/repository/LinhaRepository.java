package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinhaRepository extends JpaRepository<Linha, LinhaId> {

    @Query(value = """
            SELECT * FROM T_LIN_LINHA LIN
            WHERE LIN.LIN_LINHA_ID = :linhaId
            AND LIN.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND LIN.GLB_MUNICIPIO_COD = :municipio
            """,
            nativeQuery = true)
    Optional<Linha> findByLinhaId(@Param("linhaId") String linhaId,
                                       @Param("linhaAtendimento") String linhaAtendimento,
                                       @Param("municipio") Long municipio);

    @Query(value = """
            SELECT LIN.* FROM T_LIN_LINHA LIN
            LEFT JOIN T_LIN_LINHA_OPERADOR LOP
            ON  LOP.LIN_LINHA_ID = LIN.LIN_LINHA_ID
            AND LOP.LIN_LINHA_ATENDIMENTO = LIN.LIN_LINHA_ATENDIMENTO
            AND LOP.GLB_MUNICIPIO_COD = LIN.GLB_MUNICIPIO_COD
            WHERE LIN.LIN_LINHA_ID = :linhaId
            AND LIN.LIN_LINHA_ATENDIMENTO = :linhaAtendimento
            AND LIN.GLB_MUNICIPIO_COD = :municipio
            AND LOP.GLB_OPERADOR_CNPJ = :cnpj
            """,
            nativeQuery = true)
    Optional<Linha> findByLinhaIdAndOperador(
            @Param("linhaId") String linhaId,
            @Param("linhaAtendimento") String linhaAtendimento,
            @Param("municipio") Long municipio,
            @Param("cnpj") String cnpj
    );
}
