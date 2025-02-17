package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.linha.Parada;
import neo.com.br.CitMobi.models.records.linha.ParadaRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.repository.RotaRepository;
import neo.com.br.CitMobi.repository.LinhaRepository;
import neo.com.br.CitMobi.repository.ParadaRepository;
import neo.com.br.CitMobi.repository.ItinerarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParadaService {

    private static final Logger logger = LoggerFactory.getLogger(ParadaService.class);

    private final LinhaRepository linhaRepository;
    private final RotaRepository rotaRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final ParadaRepository paradaRepository;

    public ParadaService(RotaRepository rotaRepository, ItinerarioRepository itinerarioRepository, LinhaRepository linhaRepository, ParadaRepository paradaRepository) {
        this.rotaRepository = rotaRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.linhaRepository = linhaRepository;
        this.paradaRepository = paradaRepository;
    }

    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadasByMunicipio(Long municipio) {
        try {
            Optional<List<Parada>> optionalParadas = paradaRepository.findByMunicipio(municipio);
            if(optionalParadas.isEmpty()) {
                logger.error("Nenhuma parada encontrada para esse endereço.");
                GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("404", "Nenhuma parada encontrada para esse municipio.", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            List<ParadaRecord> paradas = optionalParadas.get()
                    .stream()
                    .map(Parada::toRecord)
                    .toList(); // Converts the stream into a List
            logger.error("Paradas encontradas: {}", paradas);
            GenericResponse<List<ParadaRecord>> response = new GenericResponse<>("200", "Paradas encontradas", paradas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao procurar a parada", e);
            GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<List<ParadaRecord>>> getParadasByLogradouro(String logradouro, Long municipio) {
        try {
            Optional<List<Parada>> optionalParadas = paradaRepository.findByLogradouro(logradouro, municipio);
            if(optionalParadas.isEmpty()) {
                logger.error("Nenhuma parada encontrada para esse endereço.");
                GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("404", "Nenhuma parada encontrada para esse endereço.", null);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            List<ParadaRecord> paradas = optionalParadas.get()
                    .stream()
                    .map(Parada::toRecord)
                    .toList(); // Converts the stream into a List
            logger.error("Paradas encontradas: {}", paradas);
            GenericResponse<List<ParadaRecord>> response = new GenericResponse<>("200", "Paradas encontradas", paradas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Erro ao procurar a parada", e);
            GenericResponse<List<ParadaRecord>> errorResponse = new GenericResponse<>("500", "Certifique-se que essa rua existe.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<GenericResponse<List<GenericResponse<ParadaRecord>>>> createParadas(List<ParadaRecord> paradasList) {
        try {
            List<GenericResponse<ParadaRecord>> createdParadas = new ArrayList<>();
            for(ParadaRecord paradaRecord : paradasList) {
                Parada criarParada = paradaRecord.toParada();
                try {
                    paradaRepository.save(criarParada);
                    ParadaRecord paradaCriada = criarParada.toRecord();
                    GenericResponse<ParadaRecord> criaParadaResponse = new GenericResponse<>("200", "Parada criada com sucesso.", paradaCriada);
                    createdParadas.add(criaParadaResponse);
                } catch (Exception e) {
                    GenericResponse<ParadaRecord> criaParadaResponse = new GenericResponse<>("500", "Erro ao criar parada, verifique.", paradaRecord);
                    createdParadas.add(criaParadaResponse);
                }
            }
            GenericResponse<List<GenericResponse<ParadaRecord>>> response = new GenericResponse<>("200", "Processo realizado com sucesso.", createdParadas);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch(Exception e) {
            logger.error("Erro ao criar as paradas", e);
            GenericResponse<List<GenericResponse<ParadaRecord>>> errorResponse = new GenericResponse<>("500", "Erro ao criar as paradas.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}