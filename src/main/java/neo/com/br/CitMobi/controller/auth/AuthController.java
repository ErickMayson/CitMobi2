package neo.com.br.CitMobi.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import neo.com.br.CitMobi.controller.linha.LinhaController;
import neo.com.br.CitMobi.models.records.usuario.AuthRecord;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.services.TokenService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
@CrossOrigin(value = "*")
@Tag(name = "auth", description = "Controller to manage authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private  final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthRecord user) {
        var userPassword = new UsernamePasswordAuthenticationToken(user.login(), user.senha());
        var auth = this.authenticationManager.authenticate(userPassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid String login) {
        if(this.usuarioRepository.findByLogin(login) != null) return ResponseEntity.badRequest().body("SORRY! CANT DO!");

        String encryptedPassword = new BCryptPasswordEncoder().encode(login);
        Usuario novoUsuario = new Usuario(login, encryptedPassword, UsuarioRole.USER);


        return ResponseEntity.badRequest().body("Sorry, you can't create users this way.");
    }

}
