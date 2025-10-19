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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
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

    public ResponseEntity<GenericResponse> getUsuarioByLogin(String authHeader, String login) {
        Optional<Usuario> usuario = usuarioRepository.findByOperadorCnpjAndLoginAndFlagAtivo(tokenService.getOperadorIdFromToken(authHeader), login, "S");

        List<UsuarioLiteRecord> liteUsuarios = usuario.stream()
                .map(UsuarioLiteRecord::fromUsuario)
                .toList();

        GenericResponse<List<UsuarioLiteRecord>> response = new GenericResponse<>("200", "Usuario encontrados", liteUsuarios);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<GenericResponse> getUsuariosPerOperador(String authHeader) {
        List<Usuario> usuarios = usuarioRepository.findByOperadorCnpjAndFlagAtivo(tokenService.getOperadorIdFromToken(authHeader), "S");

        List<UsuarioLiteRecord> liteUsuarios = usuarios.stream()
                .map(UsuarioLiteRecord::fromUsuario)
                .toList();

        GenericResponse<List<UsuarioLiteRecord>> response = new GenericResponse<>("200", "Usuarios encontrados", liteUsuarios);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<GenericResponse> createUsuario(UsuarioRecord novoUsuario, String authHeader) {
        if(tokenService.getOperadorIdFromToken(authHeader) != novoUsuario.operador().getCnpj()) {
            GenericResponse errorResponse = new GenericResponse<>("403", "Permissão insuficiente", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    public ResponseEntity<GenericResponse> updateUsuario(String login, UsuarioRecord updateData, String authHeader) {
        Usuario usuario = usuarioRepository.findUserByLogin(login, tokenService.getOperadorIdFromToken(authHeader))
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        updateUsuarioFields(usuario, updateData);

        usuario.setDataUpdate(Instant.now());
        usuarioRepository.save(usuario);

        GenericResponse response = new GenericResponse<>("200", "Usuario atualizado com sucesso!", null);
        return ResponseEntity.ok(response);
    }

    private void updateUsuarioFields(Usuario usuario, UsuarioRecord updateData) {
        Field[] fields = UsuarioRecord.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object newValue = field.get(updateData);
                if (newValue != null) {
                    Field usuarioField = Usuario.class.getDeclaredField(field.getName());
                    usuarioField.setAccessible(true);
                    Object currentValue = usuarioField.get(usuario);

                    if (field.getName().equals("senha")) {
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        if (!encoder.matches((String) newValue, (String) currentValue)) {
                            usuarioField.set(usuario, encoder.encode((String) newValue));
                        }
                    } else if (!newValue.equals(currentValue)) {
                        usuarioField.set(usuario, newValue);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }


}
