package neo.com.br.CitMobi.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.com.br.CitMobi.models.linha.Operador;
import neo.com.br.CitMobi.models.records.usuario.AuthRecord;
import neo.com.br.CitMobi.models.records.usuario.RefreshTokenRequest;
import neo.com.br.CitMobi.models.usuario.Usuario;
import neo.com.br.CitMobi.models.usuario.UsuarioRole;
import neo.com.br.CitMobi.repository.OperadorRepository;
import neo.com.br.CitMobi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Operador citMobiOperador;
    private Operador gatoPretoOperador;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        operadorRepository.deleteAll();

        citMobiOperador = new Operador();
        citMobiOperador.setCnpj("01234567890123");
        citMobiOperador.setRazaoSocial("Cit Mobi Tecnologia LTDA");
        citMobiOperador.setFlagRegulador("S");
        citMobiOperador = operadorRepository.save(citMobiOperador);

        gatoPretoOperador = new Operador();
        gatoPretoOperador.setCnpj("60870847000159");
        gatoPretoOperador.setRazaoSocial("Viacao Gato Preto LTDA");
        gatoPretoOperador.setFlagRegulador("N");
        gatoPretoOperador = operadorRepository.save(gatoPretoOperador);

        // Seed emferreira (1234)
        Usuario emferreira = new Usuario();
        emferreira.setId(UUID.fromString("00000000-0000-0000-0001-000000000003"));
        emferreira.setLogin("emferreira");
        emferreira.setSenha(passwordEncoder.encode("1234"));
        emferreira.setEmail("emferreira@citmobi.com.br");
        emferreira.setNome("ERICK MAYSON FERREIRA");
        emferreira.setRole(UsuarioRole.ADMIN);
        emferreira.setOperador(citMobiOperador);
        emferreira.setFlagAtivo("S");
        usuarioRepository.save(emferreira);

        // Seed Mobiadm (mobibrasil)
        Usuario mobiadm = new Usuario();
        mobiadm.setId(UUID.fromString("00000000-0000-0000-0001-000000000001"));
        mobiadm.setLogin("Mobiadm");
        mobiadm.setSenha(passwordEncoder.encode("mobibrasil"));
        mobiadm.setEmail("citmobi@citmobi.com.br");
        mobiadm.setNome("ADMINISTRADOR DO SISTEMA");
        mobiadm.setRole(UsuarioRole.ADMIN);
        mobiadm.setOperador(citMobiOperador);
        mobiadm.setFlagAtivo("S");
        usuarioRepository.save(mobiadm);

        // Seed admin (amarelo1)
        Usuario admin = new Usuario();
        admin.setId(UUID.fromString("00000000-0000-0000-0001-000000000002"));
        admin.setLogin("admin");
        admin.setSenha(passwordEncoder.encode("amarelo1"));
        admin.setEmail("admin@citmobi.com.br");
        admin.setNome("ADMINISTRADOR CIT MOBI");
        admin.setRole(UsuarioRole.ADMIN);
        admin.setOperador(citMobiOperador);
        admin.setFlagAtivo("S");
        usuarioRepository.save(admin);

        // Seed gtpadm (gtpadm)
        Usuario gtpadm = new Usuario();
        gtpadm.setId(UUID.fromString("00000000-0000-0000-0002-000000000001"));
        gtpadm.setLogin("gtpadm");
        gtpadm.setSenha(passwordEncoder.encode("gtpadm"));
        gtpadm.setEmail("ti_gatopreto@gatopreto.com");
        gtpadm.setNome("ADMINISTRADOR GATO PRETO");
        gtpadm.setRole(UsuarioRole.ADMIN);
        gtpadm.setOperador(gatoPretoOperador);
        gtpadm.setFlagAtivo("S");
        usuarioRepository.save(gtpadm);
    }

    @Test
    @DisplayName("Should login successfully with user emferreira and password 1234")
    void testLoginEmferreira() throws Exception {
        AuthRecord payload = new AuthRecord("emferreira", "1234");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should login successfully with user Mobiadm and password mobibrasil")
    void testLoginMobiadm() throws Exception {
        AuthRecord payload = new AuthRecord("Mobiadm", "mobibrasil");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should login successfully with case-insensitive username mobiadm")
    void testLoginCaseInsensitive() throws Exception {
        AuthRecord payload = new AuthRecord("mobiadm", "mobibrasil");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should login successfully with admin and password amarelo1")
    void testLoginAdmin() throws Exception {
        AuthRecord payload = new AuthRecord("admin", "amarelo1");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should login successfully with gtpadm and password gtpadm")
    void testLoginGtpadm() throws Exception {
        AuthRecord payload = new AuthRecord("gtpadm", "gtpadm");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should reject login with invalid password")
    void testLoginInvalidPassword() throws Exception {
        AuthRecord payload = new AuthRecord("emferreira", "wrongpassword");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should reject login with nonexistent username")
    void testLoginNonexistentUser() throws Exception {
        AuthRecord payload = new AuthRecord("nonexistent", "1234");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshToken() throws Exception {
        AuthRecord loginPayload = new AuthRecord("emferreira", "1234");

        MvcResult result = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        var jsonNode = objectMapper.readTree(responseJson);
        String refreshToken = jsonNode.get("refreshToken").asText();
        RefreshTokenRequest refreshPayload = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.refreshToken").isString());
    }

    @Test
    @DisplayName("Should reject access to /v1/api/usuarios without Bearer token with 403")
    void testGetUsuariosUnauthenticated() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/api/usuarios?login=Mobiadm"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow access to /v1/api/usuarios with valid Bearer token")
    void testGetUsuariosAuthenticated() throws Exception {
        // First login to get a valid token
        AuthRecord loginPayload = new AuthRecord("emferreira", "1234");
        MvcResult loginResult = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andReturn();

        var jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = jsonNode.get("token").asText();

        // Perform GET /v1/api/usuarios?login=Mobiadm with Bearer token
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v1/api/usuarios?login=Mobiadm")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"));
    }
}
