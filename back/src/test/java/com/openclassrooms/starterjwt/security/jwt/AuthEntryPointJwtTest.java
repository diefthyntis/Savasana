package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

class AuthEntryPointJwtTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testCommence() throws IOException, ServletException {
        // Arrange
        AuthenticationException authException = mock(AuthenticationException.class);
        when(authException.getMessage()).thenReturn("Unauthorized access");

        request.setServletPath("/test-path");

        // Act
        authEntryPointJwt.commence(request, response, authException);

        // Assert
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals(401, response.getStatus());

        // Parse the JSON response body
        ObjectMapper mapper = new ObjectMapper();
        /*
        Map<String, Object> body = mapper.readValue(response.getContentAsByteArray(), Map.class);
        */
        Map<String, Object> body = mapper.readValue(
                response.getContentAsByteArray(),
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            );


        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Unauthorized access", body.get("message"));
        assertEquals("/test-path", body.get("path"));

        // Verify logger was called
        //verify(logger).error("Unauthorized error: {}", "Unauthorized access");
    }
}


/*
 * Explications :
Mockito :

Le test utilise Mockito pour simuler les dépendances, comme le logger (Logger) et l'exception (AuthenticationException).
MockitoAnnotations.openMocks(this) initialise les mocks avant chaque test.
MockHttpServletRequest et MockHttpServletResponse :

Ces classes permettent de simuler des requêtes et des réponses HTTP sans dépendre d'un serveur réel.
ObjectMapper :

L'objet ObjectMapper est utilisé pour analyser le corps JSON de la réponse et vérifier son contenu.
Vérification des assertions :

Le test vérifie le type de contenu, le code d'état HTTP, et les champs JSON de la réponse.
Vérification du logger :

verify(logger).error(...) garantit que le message d'erreur est bien enregistré dans les logs.

*/