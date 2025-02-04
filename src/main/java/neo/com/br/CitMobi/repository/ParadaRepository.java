package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.linha.Rota;
import neo.com.br.CitMobi.models.linha.RotaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ParadaRepository extends JpaRepository<Parada, Long> {

    @Query(value = "SELECT * FROM T_LIN_PARADA PAR WHERE UPPER(PAR.LIN_PARADA_LOGRADOURO) LIKE UPPER(CONCAT('%', :logradouro, '%')) AND PAR.GLB_MUNICIPIO_COD = :municipio", nativeQuery = true)
    Optional<List<Parada>> findByLogradouro(@Param("logradouro") String logradouro, @Param("municipio") Long municipio);

    @Query(value = "SELECT * FROM T_LIN_PARADA PAR WHERE PAR.GLB_MUNICIPIO_COD = :municipio", nativeQuery = true)
    Optional<List<Parada>> findByMunicipio(@Param("municipio") Long municipio);

}
