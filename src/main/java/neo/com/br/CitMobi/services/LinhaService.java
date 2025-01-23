package neo.com.br.CitMobi.services;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.LinhaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class LinhaService {
    // TODO
    // Create CRUD Methods
    // Be able to create Linha with paradas
    // Be able to create Linha two linhas at once with inverse routes.
    // Be able to create Linha and create new paradas at the same time.
    // User notifications when line receives a modification


    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);

    private final LinhaRepository linhaRepository;

    public LinhaService(LinhaRepository linhaRepository) {
        this.linhaRepository = linhaRepository;
    }

    public ResponseEntity<LinhaResponse> getLinha(String cnpj, String municipio, String linhaId, String atendimento) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");

            Optional<Linha> existsLinha = linhaRepository.findByIdAndOperador(linhaId, atendimento, Long.parseLong(municipio), cnpj);
            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "";
                message = "A linha " + linhaId + "/" + atendimento + " não existe.";
                LinhaResponse errorResponse = new LinhaResponse("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else {
                LinhaResponse response = new LinhaResponse("201", "Linha encontrada", existsLinha.get());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            LinhaResponse errorResponse = new LinhaResponse("500", "Erro ao acessar a linha, certifique-se que a linha existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            if(novaLinha.getFlagAtiva() == null){
                novaLinha.setFlagAtiva("S");
            }

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

    public ResponseEntity<LinhaEditResponse> editLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            Linha novaLinha = linhaRecord.toLinha();

            Optional<Linha> existsLinha = linhaRepository.findByIdAndOperador(novaLinha.getLinhaId().getLinhaId(),novaLinha.getLinhaId().getLinhaAtendimento(),novaLinha.getLinhaId().getMunicipio(), novaLinha.getLinhaId().getOperador().getCnpj());

            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "";
                message = "A linha " + novaLinha.getLinhaId().getLinhaId() + "-" + novaLinha.getLinhaId().getLinhaAtendimento() + " " + novaLinha.getLinhaDescricao() + " não existe.";
                LinhaEditResponse errorResponse = new LinhaEditResponse("404", message, null, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha editLinha = new Linha();

            String linhaDescricao = novaLinha.getLinhaDescricao() != null ? novaLinha.getLinhaDescricao() : existsLinha.get().getLinhaDescricao();
            String flagIM = novaLinha.getFlagIntermunicipal() != null ? novaLinha.getFlagIntermunicipal() : existsLinha.get().getFlagIntermunicipal();
            String flagMetro = novaLinha.getFlagMetro() != null ? novaLinha.getFlagMetro() : existsLinha.get().getFlagMetro();
            String flagTrem = novaLinha.getFlagTrem() != null ? novaLinha.getFlagTrem() : existsLinha.get().getFlagTrem();
            String flagAtiva = novaLinha.getFlagAtiva() != null ? novaLinha.getFlagAtiva() : existsLinha.get().getFlagAtiva();

            editLinha.setLinhaId(novaLinha.getLinhaId());
            editLinha.setLinhaDescricao(linhaDescricao);
            editLinha.setFlagIntermunicipal(flagIM);
            editLinha.setFlagMetro(flagMetro);
            editLinha.setFlagTrem(flagTrem);
            editLinha.setFlagAtiva(flagAtiva);

            logger.warn("Edited Linha: {}", editLinha);

            linhaRepository.save(editLinha);

            LinhaEditResponse response = new LinhaEditResponse("202", "Linha editada com sucesso!", editLinha, existsLinha.get());
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error editing Linha: ", e);
            LinhaEditResponse errorResponse = new LinhaEditResponse("500", "Erro ao editar a linha.", null, null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
