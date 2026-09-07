package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    Optional<Operador> findByCnpj(String cnpj);

    List<Operador> findByCnpjIn(List<String> cnpjs);

    boolean existsByCnpj(String cnpj);
}

