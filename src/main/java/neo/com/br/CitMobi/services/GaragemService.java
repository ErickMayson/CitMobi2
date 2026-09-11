package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.veiculo.GaragemRecord;
import neo.com.br.CitMobi.models.veiculo.Garagem;
import neo.com.br.CitMobi.repository.veiculo.GaragemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GaragemService {

    private static final Logger logger = LoggerFactory.getLogger(GaragemService.class);

    private final GaragemRepository garagemRepository;

    public GaragemService(GaragemRepository garagemRepository) {
        this.garagemRepository = garagemRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<GaragemRecord>>> getAllGaragens(Long operadorId, Long municipioCod) {
        try {
            List<Garagem> garagens;
            if (operadorId != null) {
                garagens = garagemRepository.findByOperador_Id(operadorId);
            } else if (municipioCod != null) {
                garagens = garagemRepository.findByMunicipio_CodIbge(municipioCod);
            } else {
                garagens = garagemRepository.findAll();
            }

            List<GaragemRecord> records = garagens.stream()
                    .map(GaragemRecord::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new GenericResponse<>("200", "Garagens recuperadas com sucesso", records));
        } catch (Exception e) {
            logger.error("Erro ao buscar garagens: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao buscar garagens: " + e.getMessage(), null));
        }
    }
}
