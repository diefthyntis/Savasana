package com.openclassrooms.starterjwt.services;


import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    // Création d'une instance mock de UserRepository
    @Mock
    private UserRepository userRepository;

    // Injection automatique du mock UserRepository dans l'instance de UserService
    @InjectMocks
    private UserService userService;

    // Méthode exécutée avant chaque test pour initialiser les mocks
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test pour vérifier le fonctionnement de la méthode delete()
    @Test
    void testDelete() {
        Long userId = 1L;

        // Appel de la méthode à tester
        userService.delete(userId);

        // Vérification que la méthode deleteById() du repository a été appelée avec le bon argument
        verify(userRepository, times(1)).deleteById(userId);
    }

    // Test pour vérifier le fonctionnement de la méthode findById() lorsque l'utilisateur existe
    @Test
    void testFindByIdWhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // Définir le comportement du mock pour retourner un utilisateur
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Appel de la méthode à tester
        User foundUser = userService.findById(userId);

        // Vérifications
        assertNotNull(foundUser); // Vérifie que l'utilisateur trouvé n'est pas null
        assertEquals(userId, foundUser.getId()); // Vérifie que l'ID correspond
        verify(userRepository, times(1)).findById(userId); // Vérifie que findById() a été appelé une fois
    }

    // Test pour vérifier le fonctionnement de la méthode findById() lorsque l'utilisateur n'existe pas
    @Test
    void testFindByIdWhenUserDoesNotExist() {
        Long userId = 1L;

        // Définir le comportement du mock pour retourner un Optional vide
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Appel de la méthode à tester
        User foundUser = userService.findById(userId);

        // Vérification que l'utilisateur retourné est null
        assertNull(foundUser);
        verify(userRepository, times(1)).findById(userId); // Vérifie que findById() a été appelé une fois
    }
}
