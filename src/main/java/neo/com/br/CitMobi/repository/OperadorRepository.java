package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import neo.com.br.CitMobi.models.linha.LinhaOperador;
import neo.com.br.CitMobi.models.linha.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OperadorRepository extends JpaRepository<Operador, String> {

    @Query(value = """
            SELECT * FROM T_GLB_OPERADOR OPE
            WHERE OPE.GLB_OPERADOR_CNPJ IN :cnpj
            """,
            nativeQuery = true)
    Optional<List<Operador>> findByCnpj(
            @Param("cnpj") List<String> cnpj
    );

}
