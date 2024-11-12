package com.openclassrooms.starterjwt.services;


import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    // Simule le TeacherRepository pour éviter l'accès à la base de données réelle
    @Mock
    private TeacherRepository teacherRepository;

    // Injecte le mock de TeacherRepository dans l'instance de TeacherService
    @InjectMocks
    private TeacherService teacherService;
    // Sans l'injection de teacherRepository: les tests ne se comporteront pas correctement. 
    // L'objet TeacherService ne saura pas utiliser le mock, ce qui signifie que les méthodes testées ne seront pas exécutées 
    // de manière contrôlée avec les comportements simulés que vous avez définis.
    // Sans l'injection, même si vous avez configuré le comportement du mock (when(...).thenReturn(...)), 
    // le test n'utilisera pas ces configurations, rendant les vérifications avec verify(...) inopérantes ou sans effet.
    // @InjectMocks est essentiel pour que l'instance de TeacherService dans le test utilise le TeacherRepository simulé. 
    

    // Initialise les mocks avant chaque test
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Teste la méthode findAll() pour s'assurer qu'elle retourne tous les enseignants
    @Test
    void testFindAll() {
        // Arrange: Crée des objets Teacher simulés et configure le mock pour retourner ces objets
        Teacher teacher1 = new Teacher();
        Teacher teacher2 = new Teacher();
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

        // Act: Appelle la méthode findAll() de TeacherService
        List<Teacher> result = teacherService.findAll();

        // Assert: Vérifie que la liste retournée contient le bon nombre d'éléments et que le mock a été appelé
        assertEquals(2, result.size()); // Vérifie que deux enseignants sont retournés
        verify(teacherRepository, times(1)).findAll(); // Vérifie que findAll() a été appelé exactement une fois
    }

    // Teste la méthode findById() pour un ID existant
    @Test
    void testFindById_Found() {
        // Arrange: Crée un objet Teacher avec un ID spécifique et configure le mock pour le retourner
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act: Appelle la méthode findById() de TeacherService
        Teacher result = teacherService.findById(1L);

        // Assert: Vérifie que l'enseignant retourné a le bon ID et que le mock a été appelé correctement
        assertEquals(1L, result.getId()); // Vérifie que l'ID est correct
        verify(teacherRepository, times(1)).findById(1L); // Vérifie que findById() a été appelé exactement une fois avec l'ID 1L
    }

 // Teste la méthode findById() pour un ID inexistant
    @Test
    void testFindById_NotFound() {
        // Arrange: Configure le mock pour retourner un Optional vide
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act: Appelle la méthode findById() de TeacherService
        Teacher result = teacherService.findById(1L);

        // Assert: Vérifie que le résultat est null et que le mock a été appelé correctement
        assertNull(result); // Vérifie que le résultat est null pour un ID non trouvé
        verify(teacherRepository, times(1)).findById(1L); // Vérifie que findById() a été appelé exactement une fois avec l'ID 1L
    }
}
