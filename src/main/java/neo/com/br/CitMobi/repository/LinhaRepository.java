package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Linha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinhaRepository extends JpaRepository<Linha, Long>, JpaSpecificationExecutor<Linha> {

    Optional<Linha> findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
            String codigoLinha, String atendimento, Long municipioCod
    );

    List<Linha> findByMunicipio_CodIbge(Long municipioCod);

    List<Linha> findByOperador_Id(Long operadorId);

    List<Linha> findByOperador_Cnpj(String cnpj);
}

