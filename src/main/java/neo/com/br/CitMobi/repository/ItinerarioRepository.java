package neo.com.br.CitMobi.repository;

import neo.com.br.CitMobi.models.linha.Itinerario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    List<Itinerario> findByRota_IdOrderBySequenciaAsc(Long rotaId);

    List<Itinerario> findByRota_IdInOrderByRota_IdAscSequenciaAsc(List<Long> rotaIds);

    List<Itinerario> findByRota_Linha_IdOrderByRota_SentidoAscSequenciaAsc(Long linhaId);
}
