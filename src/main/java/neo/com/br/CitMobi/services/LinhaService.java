package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.Linha;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.linha.LinhaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.response.LinhaEditResponse;
import neo.com.br.CitMobi.models.records.response.LinhaResponse;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.ibge.MunicipioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LinhaService {

    private static final Logger logger = LoggerFactory.getLogger(LinhaService.class);

    private final LinhaRepository linhaRepository;
    private final OperadorRepository operadorRepository;
    private final MunicipioRepository municipioRepository;

    public LinhaService(LinhaRepository linhaRepository,
                        OperadorRepository operadorRepository,
                        MunicipioRepository municipioRepository) {
        this.linhaRepository = linhaRepository;
        this.operadorRepository = operadorRepository;
        this.municipioRepository = municipioRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<List<LinhaResponse>>> getAllLinhas(Long municipioCod, Long operadorId) {
        try {
            List<Linha> linhas;
            if (municipioCod != null && operadorId != null) {
                linhas = linhaRepository.findByMunicipio_CodIbge(municipioCod).stream()
                        .filter(l -> l.getOperador() != null && l.getOperador().getId().equals(operadorId))
                        .collect(Collectors.toList());
            } else if (municipioCod != null) {
                linhas = linhaRepository.findByMunicipio_CodIbge(municipioCod);
            } else if (operadorId != null) {
                linhas = linhaRepository.findByOperador_Id(operadorId);
            } else {
                linhas = linhaRepository.findAll();
            }

            List<LinhaResponse> responseList = linhas.stream().map(linha -> {
                List<Operador> operadores = linha.getOperador() != null ? List.of(linha.getOperador()) : Collections.emptyList();
                return new LinhaResponse(linha, operadores);
            }).collect(Collectors.toList());

            return ResponseEntity.ok(new GenericResponse<>("200", "Linhas recuperadas com sucesso", responseList));
        } catch (Exception e) {
            logger.error("Erro ao buscar linhas: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>("500", "Erro ao buscar linhas: " + e.getMessage(), null));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<GenericResponse<LinhaResponse>> getLinha(String municipio, String linhaId, String atendimento) {
        try {
            Long municipioCod = Long.parseLong(municipio);
            Optional<Linha> existsLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linhaId, atendimento, municipioCod
            );

            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "A linha " + linhaId + "/" + atendimento + " não existe.";
                GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha linha = existsLinha.get();
            List<Operador> operadores = linha.getOperador() != null ? List.of(linha.getOperador()) : Collections.emptyList();
            LinhaResponse linhaResponse = new LinhaResponse(linha, operadores);
            GenericResponse<LinhaResponse> response = new GenericResponse<>("200", "Linha encontrada", linhaResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao acessar a linha: ", e);
            GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("500", "Erro ao acessar a linha.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<LinhaResponse>> createNewLinha(LinhaRecord linhaRecord) {
        try {
            Optional<Linha> existsLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio()
            );

            if (existsLinha.isPresent()) {
                logger.error("Line already exists");
                String message = "A linha " + linhaRecord.linhaId() + "-" + linhaRecord.linhaAtendimento() + " já existe.";
                Linha linha = existsLinha.get();
                List<Operador> operadores = linha.getOperador() != null ? List.of(linha.getOperador()) : Collections.emptyList();
                GenericResponse<LinhaResponse> response = new GenericResponse<>("406", message, new LinhaResponse(linha, operadores));
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }

            Municipio municipio = municipioRepository.findById(linhaRecord.municipio()).orElse(null);
            Operador operador = null;
            if (linhaRecord.operador() != null && linhaRecord.operador().cnpj() != null) {
                operador = operadorRepository.findByCnpj(linhaRecord.operador().cnpj()).orElse(null);
            }
            if (operador == null) {
                operador = operadorRepository.findAll().stream().findFirst().orElse(null);
            }

            Linha novaLinha = new Linha(
                    linhaRecord.linhaId().trim().toUpperCase(),
                    linhaRecord.linhaAtendimento().trim().toUpperCase(),
                    municipio,
                    operador,
                    linhaRecord.linhaDescricao() != null ? linhaRecord.linhaDescricao().trim() : "",
                    linhaRecord.flagIntermunicipal() != null ? linhaRecord.flagIntermunicipal().trim().toUpperCase() : "N",
                    linhaRecord.flagMetro() != null ? linhaRecord.flagMetro().trim().toUpperCase() : "N",
                    linhaRecord.flagTrem() != null ? linhaRecord.flagTrem().trim().toUpperCase() : "N",
                    linhaRecord.flagAtiva() != null ? linhaRecord.flagAtiva().trim().toUpperCase() : "S"
            );

            novaLinha = linhaRepository.save(novaLinha);

            List<Operador> operadores = operador != null ? List.of(operador) : Collections.emptyList();
            LinhaResponse linhaResponse = new LinhaResponse(novaLinha, operadores);
            GenericResponse<LinhaResponse> response = new GenericResponse<>("201", "Linha criada com sucesso!", linhaResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error creating Linha: ", e);
            GenericResponse<LinhaResponse> errorResponse = new GenericResponse<>("500", "Erro ao criar a linha: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<GenericResponse<LinhaEditResponse>> editLinha(LinhaRecord linhaRecord) {
        try {
            Optional<Linha> existsLinha = linhaRepository.findByCodigoLinhaAndAtendimentoAndMunicipio_CodIbge(
                    linhaRecord.linhaId(), linhaRecord.linhaAtendimento(), linhaRecord.municipio()
            );

            if (existsLinha.isEmpty()) {
                logger.error("A linha procurada não existe");
                String message = "A linha " + linhaRecord.linhaId() + "-" + linhaRecord.linhaAtendimento() + " não existe.";
                GenericResponse<LinhaEditResponse> errorResponse = new GenericResponse<>("404", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            Linha linha = existsLinha.get();
            Linha anterior = new Linha(
                    linha.getCodigoLinha(),
                    linha.getAtendimento(),
                    linha.getMunicipio(),
                    linha.getOperador(),
                    linha.getLinhaDescricao(),
                    linha.getFlagIntermunicipal(),
                    linha.getFlagMetro(),
                    linha.getFlagTrem(),
                    linha.getFlagAtiva()
            );
            anterior.setId(linha.getId());

            if (linhaRecord.linhaDescricao() != null) {
                linha.setLinhaDescricao(linhaRecord.linhaDescricao().trim());
            }
            if (linhaRecord.flagIntermunicipal() != null) {
                linha.setFlagIntermunicipal(linhaRecord.flagIntermunicipal().trim().toUpperCase());
            }
            if (linhaRecord.flagMetro() != null) {
                linha.setFlagMetro(linhaRecord.flagMetro().trim().toUpperCase());
            }
            if (linhaRecord.flagTrem() != null) {
                linha.setFlagTrem(linhaRecord.flagTrem().trim().toUpperCase());
            }
            if (linhaRecord.flagAtiva() != null) {
                linha.setFlagAtiva(linhaRecord.flagAtiva().trim().toUpperCase());
            }

            linha = linhaRepository.save(linha);

            List<Operador> ops = linha.getOperador() != null ? List.of(linha.getOperador()) : Collections.emptyList();
            LinhaResponse linhaAtualizada = new LinhaResponse(linha, ops);
            LinhaResponse linhaEditada = new LinhaResponse(anterior, ops);

            LinhaEditResponse linhaEditResponse = new LinhaEditResponse(linhaAtualizada, linhaEditada);
            GenericResponse<LinhaEditResponse> response = new GenericResponse<>("200", "Linha editada com sucesso!", linhaEditResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error editing Linha: ", e);
            GenericResponse<LinhaEditResponse> errorResponse = new GenericResponse<>("500", "Erro ao editar a linha: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
