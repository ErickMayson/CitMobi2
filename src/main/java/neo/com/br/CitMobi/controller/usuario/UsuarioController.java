package neo.com.br.CitMobi.controller.usuario;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import neo.com.br.CitMobi.models.records.usuario.UsuarioRecord;
import neo.com.br.CitMobi.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/api")
@CrossOrigin(value = "*")
@Tag(name = "Usuarios", description = "Controller to manage Users.")
@PreAuthorize("!hasRole('MOTORISTA')")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity getUsuariosPerOperador(@RequestHeader("Authorization") String authHeader) {
        return usuarioService.getUsuariosPerOperador(authHeader);
    }

    @PostMapping("/usuarios")
    public ResponseEntity register(@RequestBody UsuarioRecord novoUsuario) {
        return usuarioService.createUsuario(novoUsuario);
    }



}
