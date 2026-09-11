package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.glb.OperadorRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.OperadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperadorService {

    private static final Logger logger = LoggerFactory.getLogger(OperadorService.class);

    private final OperadorRepository operadorRepository;

    public OperadorService(OperadorRepository operadorRepository) {
        this.operadorRepository = operadorRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<OperadorRecord>>> getAllOperadores() {
        try {
            List<Operador> operadores = operadorRepository.findAll();
            List<OperadorRecord> records = operadores.stream()
                    .map(OperadorRecord::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new GenericResponse<>("200", "Operadores recuperados com sucesso", records));
        } catch (Exception e) {
            logger.error("Erro ao buscar operadores: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao buscar operadores: " + e.getMessage(), null));
        }
    }
}
