package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ValidId_ReturnsOk() {
        User user = new User();
        UserDto userDto = new UserDto();  // Remplacez avec le type réel de UserDto
        
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void testFindById_InvalidId_ReturnsBadRequest() {
        ResponseEntity<?> response = userController.findById("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testFindById_NotFound_ReturnsNotFound() {
        when(userService.findById(4L)).thenReturn(null);

        ResponseEntity<?> response = userController.findById("4");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDelete_ValidId_ReturnsOk() {
        // Création d'un utilisateur à supprimer 
        User user = new User();
        user.setEmail("marcel.ayme@gmail.com");
        user.setId(3L);  

        // Simuler le comportement du service pour retourner l'utilisateur trouvé par ID
        when(userService.findById(3L)).thenReturn(user);
        
        // Mock de l'objet UserDetails pour simuler un utilisateur authentifié
        // Simuler le comportement du SecurityContext pour retourner un utilisateur authentifié 
        //avec un email qui correspond à l'utilisateur à supprimer
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("marcel.ayme@gmail.com");
        
     // Simuler l'authentification et le SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);  // Simuler l'authentification avec cet utilisateur

        // Appeler la méthode du contrôleur
        ResponseEntity<?> response = userController.delete("3");

        // Vérifier que la réponse est bien OK (200)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Vérifier que le service delete a bien été appelé une fois avec l'ID 2L
        verify(userService, times(1)).delete(3L);
    }


    @Test
    void testDelete_NotFound_ReturnsNotFound() {
        when(userService.findById(5L)).thenReturn(null);

        ResponseEntity<?> response = userController.delete("5");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    /*
    @Test
    void testDelete_Unauthorized() {
        // Simuler un utilisateur authentifié avec un email différent
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("hacker@example.com");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));

        User user = null;
		// Simuler un utilisateur dans la base de données
        when(userService.findById(1L)).thenReturn(user);

        // Effectuer la suppression
        ResponseEntity<?> response = userController.delete("1");

        // Vérifier que la réponse est 401 Unauthorized
        assertEquals(401, response.getStatusCodeValue());
    }
    */
    
    @Test
    void testDelete_InvalidIdFormat() {
        // Simuler un utilisateur authentifié
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));

        // Simuler une entrée invalide pour l'ID (non numérique)
        String invalidId = "abc"; // ID non numérique

        // Appeler la méthode delete avec un ID non valide
        ResponseEntity<?> response = userController.delete(invalidId);

        // Vérifier que la réponse renvoyée est 400 Bad Request
        assertEquals(400, response.getStatusCodeValue());
    }

    


}
