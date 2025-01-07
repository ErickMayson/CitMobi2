package neo.com.br.CitMobi.services;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class LinhaService {
    // TODO
    // Create CRUD Methods
    // Be able to create Linha with paradas
    // Be able to create Linha two linhas at once with inverse routes.
    // Be able to create Linha and create new paradas at the same time.

    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);


    public ResponseEntity<LinhaResponse> createNewLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            Linha novaLinha = linhaRecord.toLinha();

            // Log the created Linha
            logger.warn("Created Linha: {}", novaLinha);

            // Prepare the response
            LinhaResponse response = new LinhaResponse("201", "Linha criada com sucesso!", novaLinha);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error creating Linha: ", e);
            LinhaResponse errorResponse = new LinhaResponse("500", "Erro ao criar a linha.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
