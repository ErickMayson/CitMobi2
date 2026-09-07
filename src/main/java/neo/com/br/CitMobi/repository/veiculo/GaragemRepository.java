package neo.com.br.CitMobi.repository.veiculo;

import neo.com.br.CitMobi.models.veiculo.Garagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GaragemRepository extends JpaRepository<Garagem, Long> {
    List<Garagem> findByMunicipio_CodIbge(Long municipioCod);
    List<Garagem> findByOperador_Id(Long operadorId);
    List<Garagem> findByOperador_Cnpj(String cnpj);
}

