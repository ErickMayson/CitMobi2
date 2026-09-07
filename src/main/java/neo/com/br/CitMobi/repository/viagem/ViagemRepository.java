package neo.com.br.CitMobi.repository.viagem;

import neo.com.br.CitMobi.models.viagem.Viagem;
import neo.com.br.CitMobi.models.viagem.ViagemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {
    List<Viagem> findByMotorista_Id(UUID motoristaId);
    List<Viagem> findByVeiculo_Id(Long veiculoId);
    List<Viagem> findByLinha_Id(Long linhaId);
    List<Viagem> findByStatus(ViagemStatus status);
    Optional<Viagem> findFirstByVeiculo_IdAndStatus(Long veiculoId, ViagemStatus status);
    Optional<Viagem> findFirstByMotorista_IdAndStatus(UUID motoristaId, ViagemStatus status);
}
