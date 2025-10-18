package neo.com.br.CitMobi.services;

import neo.com.br.CitMobi.models.records.response.GenericResponse;
import neo.com.br.CitMobi.models.records.usuario.UsuarioLiteRecord;
import neo.com.br.CitMobi.models.records.usuario.UsuarioRecord;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public UsuarioService(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public ResponseEntity<GenericResponse> getUsuariosPerOperador(String authHeader) {
        String cnpjOperador = tokenService.getOperadorIdFromToken(authHeader);
        List<Usuario>  usuarios = usuarioRepository.findByOperadorCnpj(cnpjOperador);

        List<UsuarioLiteRecord> liteUsuarios = usuarios.stream()
                .map(UsuarioLiteRecord::fromUsuario)
                .toList();

        GenericResponse<List<UsuarioLiteRecord>> response = new GenericResponse<>("200", "Usuarios encontrados", liteUsuarios);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<GenericResponse> createUsuario(UsuarioRecord novoUsuario) {
        try {
            Optional<String> usuarioExists = usuarioRepository.getUsuario(novoUsuario.login(), novoUsuario.email(), novoUsuario.telefone(), novoUsuario.cpf());
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
