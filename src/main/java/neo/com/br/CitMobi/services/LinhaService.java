package neo.com.br.CitMobi.services;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.LinhaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class LinhaService {
    // TODO
    // Create CRUD Methods
    // Be able to create Linha with paradas
    // Be able to create Linha two linhas at once with inverse routes.
    // Be able to create Linha and create new paradas at the same time.

    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);

    private final LinhaRepository linhaRepository;

    public LinhaService(LinhaRepository linhaRepository) {
        this.linhaRepository = linhaRepository;
    }


    public ResponseEntity<LinhaResponse> createNewLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            Linha novaLinha = linhaRecord.toLinha();

            Optional<Linha> existsLinha = linhaRepository.findById(novaLinha.getLinhaId());
            if(existsLinha.isPresent()) {
                logger.error("Line already exists");
                String message = "";
                if(existsLinha.get().getFlagAtiva().equals("S")) {
                    message = "A linha " + novaLinha.getLinhaId().getLinhaId() + "-" + novaLinha.getLinhaId().getLinhaAtendimento() + " " + novaLinha.getLinhaDescricao() + " já existe.";
                } else {
                    message = "A linha " + novaLinha.getLinhaId().getLinhaId() + "-" + novaLinha.getLinhaId().getLinhaAtendimento() + " " + novaLinha.getLinhaDescricao() +  " está inativa.";
                }
                LinhaResponse errorResponse = new LinhaResponse("406", message, existsLinha.get());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
            }


            logger.warn("Created Linha: {}", novaLinha);

            linhaRepository.save(novaLinha);

            // Prepare the response
            LinhaResponse response = new LinhaResponse("201", "Linha criada com sucesso!", novaLinha);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error creating Linha: ", e);
            LinhaResponse errorResponse = new LinhaResponse("500", "Erro ao criar a linha.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<LinhaResponse> editLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            Linha novaLinha = linhaRecord.toLinha();

            Optional<Linha> existsLinha = linhaRepository.findByIdAndOperador(novaLinha.getLinhaId().getLinhaId(),novaLinha.getLinhaId().getLinhaAtendimento(),novaLinha.getLinhaId().getMunicipio(), novaLinha.getLinhaId().getOperador().getCnpj());

            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "";
                message = "A linha " + novaLinha.getLinhaId().getLinhaId() + "-" + novaLinha.getLinhaId().getLinhaAtendimento() + " " + novaLinha.getLinhaDescricao() + " não existe.";
                LinhaResponse errorResponse = new LinhaResponse("404", message, new Linha());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }



            logger.warn("Created Linha: {}", novaLinha);

            LinhaResponse response = new LinhaResponse("202", "Linha editada com sucesso!", novaLinha);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error editing Linha: ", e);
            LinhaResponse errorResponse = new LinhaResponse("500", "Erro ao editar a linha.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
