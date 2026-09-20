package neo.com.br.CitMobi.controller.linha;

import neo.com.br.CitMobi.models.ibge.Municipio;
import neo.com.br.CitMobi.models.linha.*;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.repository.*;
import neo.com.br.CitMobi.repository.ibge.MunicipioRepository;
import neo.com.br.CitMobi.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LinhaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private LinhaRepository linhaRepository;

    @Autowired
    private RotaRepository rotaRepository;

    @Autowired
    private ParadaRepository paradaRepository;

    @Autowired
    private ItinerarioRepository itinerarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Linha linha;

    @BeforeEach
    void setUp() {
        itinerarioRepository.deleteAll();
        rotaRepository.deleteAll();
        linhaRepository.deleteAll();
        paradaRepository.deleteAll();
        usuarioRepository.deleteAll();
        operadorRepository.deleteAll();

        Operador operador = new Operador();
        operador.setCnpj("01234567890123");
        operador.setRazaoSocial("Cit Mobi Tecnologia LTDA");
        operador.setFlagRegulador("S");
        operador = operadorRepository.save(operador);

        Municipio municipio = new Municipio();
        municipio.setCodIbge(3550308L);
        municipio.setNome("São Paulo");
        municipio.setUf("SP");
        municipio = municipioRepository.save(municipio);

        Usuario admin = new Usuario();
        admin.setId(UUID.randomUUID());
        admin.setLogin("admin");
        admin.setSenha(passwordEncoder.encode("amarelo1"));
        admin.setEmail("admin@citmobi.com.br");
        admin.setNome("ADMINISTRADOR");
        admin.setRole(UsuarioRole.ADMIN);
        admin.setOperador(operador);
        admin.setFlagAtivo("S");
        usuarioRepository.save(admin);

        adminToken = tokenService.generateToken(admin);

        linha = new Linha("175T-10", "10", municipio, operador, "Metrô Santana - Pinheiros", "N", "S", "S", "S");
        linha = linhaRepository.save(linha);

        Rota rotaIda = new Rota(linha, "Metrô Santana", "IDA");
        rotaIda = rotaRepository.save(rotaIda);

        Parada parada = new Parada("Rua Voluntarios", "500", "Obs", BigDecimal.valueOf(-46.62), BigDecimal.valueOf(-23.50), 3550308L, "SP", 1L);
        parada.setAtiva("S");
        parada = paradaRepository.save(parada);

        Itinerario it = new Itinerario(rotaIda, parada, 1);
        itinerarioRepository.save(it);
    }

    @Test
    @DisplayName("Should return 403 when no token is provided")
    void shouldReturn403WhenNoToken() throws Exception {
        mockMvc.perform(get("/v1/api/linhas/detalhes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return Linhas with Detalhes successfully")
    void shouldReturnLinhasComDetalhes() throws Exception {
        mockMvc.perform(get("/v1/api/linhas/detalhes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].linha.codigoLinha").value("175T-10"))
                .andExpect(jsonPath("$.data[0].linha.rotas[0].linhaSentido").value("IDA"))
                .andExpect(jsonPath("$.data[0].linha.rotas[0].itinerario.paradas[0].logradouro").value("Rua Voluntarios"));
    }
}
