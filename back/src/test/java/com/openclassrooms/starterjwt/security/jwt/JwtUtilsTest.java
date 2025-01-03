package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Date;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private final String jwtSecret = "testSecret";
    private final int jwtExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtils = new JwtUtils();
        // Inject mock values for jwtSecret and jwtExpirationMs
        jwtUtils.setJwtSecret(jwtSecret);
        jwtUtils.setJwtExpirationMs(jwtExpirationMs);
    }

    @Test
    void testGenerateJwtToken() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl("Marcel","Aymé","marcel.ayme@gmail.com","marcel.ayme@gmail.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetUserNameFromJwtToken() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl("Marcel","Aymé","marcel.ayme@gmail.com","lajumentverte");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals("marcel.ayme@gmail.com", username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl("Marcel","Aymé","marcel.ayme@gmail.com","lajumentverte");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        // Arrange
        jwtUtils.setJwtExpirationMs(-1); // Token already expired
        UserDetailsImpl userDetails = new UserDetailsImpl("Marcel","Aymé","marcel.ayme@gmail.com","lajumentverte");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        // Arrange
        String invalidToken = Jwts.builder()
                .setSubject("marcel.ayme@gmail.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        // Act
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.token";

        // Act
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }
    
    /*
    @Test
    void validateJwtToken_UnsupportedJwtException() {
        // Arrange
        String token = "invalidToken";
        try (MockedStatic<Jwts> mockedJwts = Mockito.mockStatic(Jwts.class)) {
            mockedJwts.when(() -> Jwts.parser().setSigningKey(jwtUtils.getJwtSecret()).parseClaimsJws(token))
                    .thenThrow(new UnsupportedJwtException("Unsupported JWT token"));

            // Act
            boolean isValid = jwtUtils.validateJwtToken(token);

            // Assert
            assertFalse(isValid);
        }
    }
     */
   

    @Test
    void validateJwtToken_IllegalArgumentException() {
        // Arrange
    	 String emptyToken = "";

         // Act
         boolean isValid = jwtUtils.validateJwtToken(emptyToken);

         // Assert
         assertFalse(isValid);
    }
    /*
    @Test
    void validateJwtToken_IllegalArgumentException() {
        // Arrange
        String token = ""; // Chaîne vide pour déclencher l'exception
        try (MockedStatic<Jwts> mockedJwts = Mockito.mockStatic(Jwts.class)) {
            mockedJwts.when(() -> Jwts.parser().setSigningKey(jwtUtils.getJwtSecret()).parseClaimsJws(token))
                    .thenThrow(new IllegalArgumentException("JWT claims string is empty"));

            // Act
            boolean isValid = jwtUtils.validateJwtToken(token);

            // Assert
            assertFalse(isValid);
        }
    }
    */
    
}

/*
Explications :
Injection des dépendances :

jwtSecret et jwtExpirationMs sont définis directement dans le test pour simuler les propriétés @Value.
Cas de test :

Génération de token JWT : Vérifie que le token est généré correctement.
Extraction du nom d'utilisateur : Vérifie que le username peut être extrait du token.
Validation de token :
Token valide.
Token expiré.
Signature invalide.
Token mal formé.
Mockito pour les dépendances :

La dépendance Authentication est simulée pour fournir un utilisateur principal (UserDetailsImpl).
Cas spécifiques :

Les tests couvrent les scénarios d'erreurs comme un token expiré, une signature invalide ou un token mal formé.
*/