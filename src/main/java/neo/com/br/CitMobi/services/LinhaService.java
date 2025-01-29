package neo.com.br.CitMobi.services;

import jakarta.validation.Valid;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.LinhaId;
import neo.com.br.CitMobi.models.linha.LinhaOperador;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.LinhaOperadorRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Collections;
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
    // Linhas com mais de um operador não podem ser alteradas, só por agencia reguladora

    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);

    private final LinhaRepository linhaRepository;
    private final LinhaOperadorRepository linhaOperadorRepository;
    private final OperadorRepository operadorRepository;

    public LinhaService(LinhaRepository linhaRepository, LinhaOperadorRepository linhaOperadorRepository, OperadorRepository operadorRepository) {
        this.linhaRepository = linhaRepository;
        this.linhaOperadorRepository = linhaOperadorRepository;
        this.operadorRepository = operadorRepository;
    }

    public ResponseEntity<GenericResponse<LinhaResponse>> getLinha(String municipio, String linhaId, String atendimento) {
        try {
            //List<String> reguladores = List.of("46392155000111", "41814509000155", "60498417000158");

            Optional<Linha> existsLinha = linhaRepository.findById(new LinhaId(linhaId, atendimento, Long.parseLong(municipio)));
            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "";
                message = "A linha " + linhaId + "/" + atendimento + " não existe.";
                LinhaResponse linhaResponse = new LinhaResponse(null, null);
                GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("404", message, linhaResponse);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else {
                LinhaId linhaInfo = existsLinha.get().getLinhaId();
                Optional<List<LinhaOperador>> linhaOperadores = linhaOperadorRepository.findByLinhaId(linhaInfo.getLinhaId(), linhaInfo.getLinhaAtendimento(), linhaInfo.getMunicipio());
                if(linhaOperadores.isEmpty()) {
                    logger.info("Linha não possui operadores");
                    LinhaResponse linhaResponse = new LinhaResponse(existsLinha.get(), null);
                    GenericResponse<LinhaResponse> response = new GenericResponse<>("200", "Linha encontrada", linhaResponse);
                    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
                } else {
                    GenericResponse<LinhaResponse> response = getLinhaResponseWithOperadores("200", "Linha encontrada", linhaOperadores.get(), existsLinha.get());
                    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            LinhaResponse linhaResponse = new LinhaResponse( null, null);
            GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha, certifique-se que a linha existe.", linhaResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static GenericResponse<LinhaResponse> getLinhaResponseWithOperadores(String status, String message, List<LinhaOperador> linhaOperadores, Linha existsLinha) {
        List<Operador> operadores = new ArrayList<>();
        linhaOperadores.forEach(linhaOperador -> {
                Operador operador = linhaOperador.getLinhaOperadorId().getOperador();
                operadores.add(operador);
        });
        LinhaResponse linhaResponse = new LinhaResponse(existsLinha, operadores);
        return new GenericResponse<>(status, message, linhaResponse);
    }


    public ResponseEntity<GenericResponse<LinhaResponse>> createNewLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            Optional<Linha> existsLinha = linhaRepository.findById(new LinhaId(linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio()));
            if(existsLinha.isPresent()) {
                logger.error("Line already exists");
                String message = "";
                if(existsLinha.get().getFlagAtiva().equals("S")) {
                    message = "A linha " + linhaRecord.linhaId() + "-" + linhaRecord.linhaAtendimento() + " " + linhaRecord.linhaDescricao() + " já existe.";
                } else {
                    message = "A linha " + linhaRecord + "-" + linhaRecord.linhaAtendimento() + " " + linhaRecord.linhaDescricao() +  " está inativa.";
                }
                Optional<List<LinhaOperador>> linhaOperadores = linhaOperadorRepository.findByLinhaId(linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio());
                if(linhaOperadores.isPresent()) {
                    // Se tiver operador, entra na função que puxa os operadores da linha.
                    GenericResponse<LinhaResponse> response = getLinhaResponseWithOperadores("406", message, linhaOperadores.get(), existsLinha.get());
                    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                } else {
                    // Se nao tiver operador, vai trazer as informacoes sem operador
                    LinhaResponse linhaResponse = new LinhaResponse(existsLinha.get(), null);
                    GenericResponse<LinhaResponse> response = new GenericResponse<>("406", "Linha criada com sucesso!", linhaResponse);
                    return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
                }

            }

            Optional<List<Operador>> operadores = operadorRepository.findByCnpj(Collections.singletonList(linhaRecord.operador().cnpj()));
            Linha novaLinha = linhaRecord.toLinha();
            logger.warn("Linha: {}", novaLinha);
            if(operadores.isPresent()){
                novaLinha.setFlagAtiva("S");
            } else {
                logger.info("Linha criada sem operadores nâo pode ser ativa.");
                novaLinha.setFlagAtiva("N");
            }
            linhaRepository.save(novaLinha);

            if(operadores.isEmpty()) {
                LinhaResponse linhaResponse = new LinhaResponse(novaLinha, null);
                GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("201", "Linha criada, adicione um operador para ativar essa linha.", linhaResponse);
                return new ResponseEntity<>(errorResponse, HttpStatus.CREATED);

            } else {
                LinhaOperador linhaOperador = new LinhaOperador(linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio(), operadores.get().get(0));
                linhaOperadorRepository.save(linhaOperador);
                GenericResponse<LinhaResponse> response = getLinhaResponseWithOperadores("201", "Linha criada com sucesso!", Collections.singletonList(linhaOperador), novaLinha);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            logger.error("Error creating Linha: ", e);
            LinhaResponse linhaResponse = new LinhaResponse(null, null);
            GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("500", "Erro ao criar a linha.", linhaResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<LinhaEditResponse>> editLinha(@RequestBody LinhaRecord linhaRecord) {
        try {
            // TO DO
            // Esta retornando a mesma linha já editada duas vezes.
            Optional<Linha> existsLinha = linhaRepository.findByLinhaIdAndOperador(linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio(), linhaRecord.operador().cnpj());
            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "A linha " + linhaRecord.linhaId() + "-" + linhaRecord.linhaAtendimento() + " " + linhaRecord.linhaDescricao() + " não existe.";
                LinhaEditResponse linhaEditResponse = new LinhaEditResponse(null, null);
                GenericResponse<LinhaEditResponse> errorResponse = new GenericResponse<>("404", message, linhaEditResponse);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha editLinha = new Linha();

            String linhaDescricao = linhaRecord.linhaDescricao() != null ? linhaRecord.linhaDescricao() : existsLinha.get().getLinhaDescricao();
            String flagIM = linhaRecord.flagIntermunicipal() != null ? linhaRecord.flagIntermunicipal() : existsLinha.get().getFlagIntermunicipal();
            String flagMetro = linhaRecord.flagMetro() != null ? linhaRecord.flagMetro() : existsLinha.get().getFlagMetro();
            String flagTrem = linhaRecord.flagTrem() != null ? linhaRecord.flagTrem() : existsLinha.get().getFlagTrem();
            String flagAtiva = linhaRecord.flagAtiva() != null ? linhaRecord.flagAtiva() : existsLinha.get().getFlagAtiva();

            editLinha.setLinhaId(new LinhaId(linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio()));
            editLinha.setLinhaDescricao(linhaDescricao);
            editLinha.setFlagIntermunicipal(flagIM);
            editLinha.setFlagMetro(flagMetro);
            editLinha.setFlagTrem(flagTrem);
            editLinha.setFlagAtiva(flagAtiva);

            logger.warn("Edited Linha: {}", editLinha);

            linhaRepository.save(editLinha);

            Optional<List<LinhaOperador>> linhaOperadores = linhaOperadorRepository.findByLinhaId(existsLinha.get().getLinhaId().getLinhaId(), existsLinha.get().getLinhaId().getLinhaAtendimento(), existsLinha.get().getLinhaId().getMunicipio());
            GenericResponse<LinhaResponse> linhaAnterior = getLinhaResponseWithOperadores("200", "Linha encontrada", linhaOperadores.get(), existsLinha.get());
            linhaOperadores = linhaOperadorRepository.findByLinhaId(editLinha.getLinhaId().getLinhaId(), editLinha.getLinhaId().getLinhaAtendimento(), editLinha.getLinhaId().getMunicipio());
            GenericResponse<LinhaResponse> linhaEditada = getLinhaResponseWithOperadores("200", "Linha encontrada", linhaOperadores.get(), editLinha);


            LinhaEditResponse linhaEditResponse = new LinhaEditResponse(linhaEditada.data(), linhaAnterior.data());
            GenericResponse<LinhaEditResponse> response = new GenericResponse<>("202", "Linha editada com sucesso!", linhaEditResponse);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.error("Error editing Linha: ", e);
            LinhaEditResponse linhaEditResponse = new LinhaEditResponse(null, null);
            GenericResponse<LinhaEditResponse> errorResponse = new GenericResponse<>("500", "Erro ao editar a linha.", linhaEditResponse);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
