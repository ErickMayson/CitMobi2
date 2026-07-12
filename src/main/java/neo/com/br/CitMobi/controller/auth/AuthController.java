package neo.com.br.CitMobi.controller.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import neo.com.br.CitMobi.controller.linha.LinhaController;
import neo.com.br.CitMobi.models.records.usuario.AuthRecord;
import neo.com.br.CitMobi.models.records.usuario.RefreshTokenRequest;
import neo.com.br.CitMobi.models.records.usuario.UsuarioRecord;
import neo.com.br.CitMobi.models.usuario.LoginResponse;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import neo.com.br.CitMobi.services.TokenService;
import neo.com.br.CitMobi.services.UsuarioService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
@CrossOrigin(value = "*")
@Tag(name = "auth", description = "Controller to manage authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthRecord user) {
        logger.info("New AUTH request for user: {}", user.login());
        var userPassword = new UsernamePasswordAuthenticationToken(user.login(), user.senha());
        var auth = this.authenticationManager.authenticate(userPassword);

        var usuario = (Usuario) auth.getPrincipal();
        var token = tokenService.generateToken(usuario);
        var refreshToken = tokenService.generateRefreshToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        logger.info("Token refresh request received");
        var subject = tokenService.validateRefreshToken(request.refreshToken());

        if (subject == null || subject.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails = usuarioRepository.findByLogin(subject);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        var usuario = (Usuario) userDetails;
        var token = tokenService.generateToken(usuario);
        var refreshToken = tokenService.generateRefreshToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token, refreshToken));
    }

}
