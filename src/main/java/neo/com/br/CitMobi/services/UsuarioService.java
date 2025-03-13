package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.records.linha.ItinerarioRecord;
import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.usuario.UsuarioRecord;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public ResponseEntity<GenericResponse> createUsuario(UsuarioRecord novoUsuario) {
        try {
            Optional<String> usuarioExists = usuarioRepository.getUsuario(novoUsuario.login(), novoUsuario.email(), novoUsuario.telefone());
            if(usuarioExists.isPresent()) {
                String message = usuarioExists.get().toUpperCase();
                logger.error(message);
                GenericResponse errorResponse = new GenericResponse<>("500", message, null);
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Usuario usuario = new Usuario(novoUsuario);
            usuarioRepository.save(usuario);
            logger.error("Usuario {} criado com sucesso.", novoUsuario.nome());
            GenericResponse response = new GenericResponse<>("201", "Usuario criado com sucesso!", null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Erro ao criar usuario", e);
            GenericResponse errorResponse = new GenericResponse<>("500", "Erro ao criar o usuario", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
