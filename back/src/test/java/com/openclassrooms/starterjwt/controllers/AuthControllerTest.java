package com.openclassrooms.starterjwt.controllers;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;




import static org.mockito.Mockito.*;



import java.util.Optional;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;




import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;


import org.junit.jupiter.api.BeforeEach;

import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;


public class AuthControllerTest {

    // Mock des dépendances pour isoler le test du contrôleur
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    // Injection des mocks dans le contrôleur à tester
    @InjectMocks
    private AuthController authController;

    // Initialisation des mocks avant chaque test
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser_Success() {
        // Préparation de la requête de connexion avec les identifiants de l'administrateur
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("yoga@studio.com");
        loginRequest.setPassword("test!1234");

        // Mock de l'authentification réussie
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Création fictive de l'utilisateur Administrateur 
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "yoga@studio.com", "Admin", "Admin", true, "test!1234");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Génération d'un JWT pour l'utilisateur authentifié
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("test-jwt-token");

        // Création d'un utilisateur avec le statut d'administrateur et récupération par email
        User user = new User("yoga@studio.com", "User", "Test", "password", true);
        when(userRepository.findByEmail("yoga@studio.com")).thenReturn(Optional.of(user));

        // Appel à la méthode authenticateUser et vérification de la réponse
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Vérification du statut de la réponse
        assertEquals(200, response.getStatusCodeValue());

        // Vérification du contenu de la réponse pour le JwtResponse
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals("test-jwt-token", jwtResponse.getToken());
        assertEquals(userDetails.getId(), jwtResponse.getId());
        assertEquals(userDetails.getUsername(), jwtResponse.getUsername());
        assertEquals(userDetails.getFirstName(), jwtResponse.getFirstName());
        assertEquals(userDetails.getLastName(), jwtResponse.getLastName());
        assertTrue(jwtResponse.getAdmin()); // Vérifie que l'utilisateur est bien administrateur
    }

    @Test
    public void testRegisterUser_Success() {
        // Préparation de la requête d'inscription pour un nouvel utilisateur
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("yoga@studio.com");
        signUpRequest.setPassword("test!1234");
        signUpRequest.setFirstName("Admin");
        signUpRequest.setLastName("Admin");

        // Vérification que l'email n'est pas déjà utilisé
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        
        // Mock de l'encodage du mot de passe
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encoded-password");

        // Appel à la méthode registerUser et vérification de la réponse
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Vérification du statut de la réponse
        assertEquals(200, response.getStatusCodeValue());

        // Vérification du contenu de la réponse pour le MessageResponse
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertNotNull(messageResponse);
        assertEquals("User registered successfully!", messageResponse.getMessage());

        // Vérifie que l'utilisateur a bien été enregistré dans le dépôt
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        // Préparation d'une requête d'inscription avec un email déjà existant
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail("yoga@studio.com");
        signUpRequest.setPassword("test!1234");
        signUpRequest.setFirstName("Admin");
        signUpRequest.setLastName("Admin");


        // Mock indiquant que l'email est déjà présent dans le dépôt
        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        // Appel à la méthode registerUser pour tester la réponse en cas de duplication d'email
        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        // Vérification du statut de la réponse pour un email déjà pris
        assertEquals(400, response.getStatusCodeValue());

        // Vérification du contenu de la réponse pour le MessageResponse
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertNotNull(messageResponse);
        assertEquals("Error: Email is already taken!", messageResponse.getMessage());

        // Vérifie que le dépôt n'a pas essayé d'enregistrer un nouvel utilisateur
        verify(userRepository, times(0)).save(any(User.class));
    }
}


