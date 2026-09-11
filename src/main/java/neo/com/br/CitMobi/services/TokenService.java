package neo.com.br.CitMobi.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import neo.com.br.CitMobi.models.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.secret}")
    String secret;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String flagRegulador = (usuario.getOperador() != null && usuario.getOperador().getFlagRegulador() != null)
                    ? usuario.getOperador().getFlagRegulador()
                    : "N";

            return JWT.create()
                    .withIssuer("cit")
                    .withSubject(usuario.getLogin())
                    .withClaim("role", usuario.getRole().getRole())
                    .withClaim("operador", usuario.getOperador() != null ? usuario.getOperador().getCnpj() : null)
                    .withClaim("flagRegulador", flagRegulador)
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("ITS OVER");
        }
    }

    public String generateRefreshToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("cit")
                    .withSubject(usuario.getLogin())
                    .withClaim("type", "refresh")
                    .withExpiresAt(getRefreshExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error generating refresh token");
        }
    }


    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("cit")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch(JWTVerificationException e) {
            return "";
        }
    }

    public String validateRefreshToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decoded = JWT.require(algorithm)
                    .withIssuer("cit")
                    .withClaim("type", "refresh")
                    .build()
                    .verify(token);
            return decoded.getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    public DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("cit")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String getOperadorIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded = decodeToken(token);
        return decoded != null ? decoded.getClaim("operador").asString() : null;
    }

    public String getFlagReguladorFromToken(String authHeader) {
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded = decodeToken(token);
        return decoded != null ? decoded.getClaim("flagRegulador").asString() : null;
    }

    public String getRoleFromToken(String authHeader) {
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded = decodeToken(token);
        return decoded != null ? decoded.getClaim("role").asString() : null;
    }

    public String getLoginFromToken(String authHeader) {
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded = decodeToken(token);
        return decoded != null ? decoded.getSubject() : null;
    }


    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant getRefreshExpirationDate() {
        return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
    }

}
