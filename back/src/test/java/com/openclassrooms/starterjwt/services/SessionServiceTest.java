package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;

import org.junit.jupiter.api.BeforeEach;

//import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
// L'utilisation d'un mock de SessionRepository au lieu d'une instance réelle de SessionRepository présente plusieurs avantages dans les tests unitaires :
// Isolation du test : L'objectif principal des tests unitaires est de tester la logique d'une méthode ou d'une classe de manière isolée, sans dépendre des autres composants du système. En utilisant un mock de SessionRepository, on peut s'assurer que le test se concentre uniquement sur le comportement de la classe SessionService, sans être influencé par la logique interne de SessionRepository.
// Contrôle des comportements : Les mocks permettent de simuler des comportements précis des dépendances. Par exemple, on peut configurer un mock pour qu'il retourne une valeur spécifique ou qu'il lève une exception lors de l'appel de certaines méthodes. Cela permet de tester comment la classe SessionService réagit dans différentes situations, comme lorsque le SessionRepository retourne null ou une liste vide.
// Test prédictible et stable : En utilisant un mock, le test devient entièrement prédictible, car il ne dépend pas d'une base de données ou d'autres ressources externes. Les résultats ne fluctuent pas en fonction de l'état des données ou des modifications dans le code du SessionRepository.
// Performance accrue : Les mocks sont généralement plus rapides que les instances réelles, car ils ne nécessitent pas d'accès à la base de données ou d'autres opérations lourdes. Cela rend les tests unitaires plus rapides et efficaces.
// Simplicité et simplicité de configuration : Utiliser un mock permet de se passer de la configuration nécessaire pour utiliser une instance réelle du SessionRepository, comme la mise en place de l'accès à la base de données, la gestion des transactions, etc.
// Exemple
// Lors d'un test unitaire, si on utilise directement une instance réelle de SessionRepository, le test dépendra de la base de données et deviendra un test d'intégration. Cela peut rendre le test plus complexe, plus lent et plus difficile à maintenir. En revanche, un mock simule le comportement sans exécuter de logique réelle, ce qui garantit que le test reste unitaire.
// En résumé, l'utilisation d'un mock de SessionRepository assure que le test se concentre sur la logique de la classe SessionService elle-même, sans dépendre de l'implémentation ni des effets de bord potentiels de SessionRepository.


class SessionServiceTest {
	
	@Mock
    private SessionRepository sessionRepository;
	
	@Mock
    private UserRepository userRepository;
	
	@InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        //sessionRepository = mock(SessionRepository.class); remplacé par @Mock
        //userRepository = mock(UserRepository.class); remplacé par @Mock
    	MockitoAnnotations.openMocks(this);
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @Test
    void testCreateSession() {
    	// Initialisation d'une instance de Session pour le test.
        Session session = new Session();
        
        // Simulation du comportement du repository : lorsque la méthode save est appelée avec 'session',
        // elle doit retourner cette même instance
        when(sessionRepository.save(session)).thenReturn(session);

        
        Session result = sessionService.create(session);

        // Vérification que le résultat n'est pas null, confirmant que l'objet a été correctement sauvegardé.
        assertNotNull(result);
        // Si l'instruction assertNotNull(result) échoue (c'est-à-dire que result est null), 
        // le test unitaire échouera et affichera une erreur. 
        // Plus précisément, l'exception AssertionError sera levée, 
        // indiquant que la condition de l'assertion n'a pas été satisfaite.
        // Cela signifie que la méthode create() du SessionService n'a pas retourné un objet valide, 
        // ce qui peut indiquer un problème dans l'implémentation de la méthode ou une défaillance dans la configuration du test
        // (par exemple, une simulation incorrecte ou une dépendance mal configurée).
        // Lorsqu'une assertion échoue, le test se termine immédiatement et l'erreur est enregistrée dans le rapport de test, 
        // permettant au développeur de voir quel test n'a pas réussi et pourquoi.
        
        verify(sessionRepository, times(1)).save(session);
        // Vérifie que la méthode `save()` du `sessionRepository` a été appelée exactement une fois avec l'objet `session`
        // comme argument. Cela confirme que la méthode `create()` du `SessionService` a bien utilisé le repository pour
        // sauvegarder la session, et que le comportement attendu a été respecté lors de l'appel du service.
       
        // L'instruction verify() ne retourne pas de valeur. 
        // C'est une méthode de vérification de Mockito qui sert uniquement à valider que certaines interactions 
        // ont eu lieu entre les mocks et la méthode testée.
        // Lorsque verify() est appelée, elle effectue une vérification et lève une exception MockitoAssertionError 
        // si la condition spécifiée n'est pas remplie (par exemple, si la méthode n'a pas été appelée le nombre de fois attendu ou avec les bons arguments). 
        // Si la condition est respectée, l'instruction se termine normalement sans générer d'erreur.
        // En résumé, verify() est une méthode de contrôle et ne retourne pas de résultat, mais elle peut entraîner l'échec du test si la vérification échoue.

    }

    @Test
    void testDeleteSession() {
    	// Déclare et initialise l'identifiant de la session à supprimer pour le test.
        Long sessionId = 1L;
        
        // Simule le comportement de la méthode `deleteById` du `sessionRepository`.
        // `doNothing()` indique que lorsque `deleteById(sessionId)` est appelée, aucune action réelle n'est effectuée.
        doNothing().when(sessionRepository).deleteById(sessionId);
        

        sessionService.delete(sessionId);

        // Vérifie que la méthode `deleteById` du `sessionRepository` a bien été appelée exactement une fois avec l'argument `sessionId`.
        // Cela confirme que la méthode `delete()` de `sessionService` a bien exécuté l'appel au repository.
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    void testFindAllSessions() {
        List<Session> sessions = new ArrayList<>();
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertEquals(sessions, result);
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Long sessionId = 1L;
        Session session = new Session();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(sessionId);

        assertNotNull(result);
        assertEquals(session, result);
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    void testGetByIdNotFound() {
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Session result = sessionService.getById(sessionId);

        assertNull(result);
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    void testUpdateSession() {
        Long sessionId = 1L;
        Session session = new Session();
        session.setId(sessionId);
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.update(sessionId, session);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipate() {
        Long sessionId = 1L;
        Long userId = 2L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());
        User user = new User();
        user.setId(userId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        sessionService.participate(sessionId, userId);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateAlreadyExists() {
        Long sessionId = 1L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(List.of(user));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    void testNoLongerParticipate() {
        Long sessionId = 1L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        Session session = new Session();
        session.setUsers(new ArrayList<>(List.of(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(sessionId, userId);

        assertFalse(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateNotParticipating() {
        Long sessionId = 1L;
        Long userId = 2L;
        Session session = new Session();
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
    }

    @Test
    void testParticipateSessionNotFound() {
        Long sessionId = 1L;
        Long userId = 2L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test
    void testParticipateUserNotFound() {
        Long sessionId = 1L;
        Long userId = 2L;
        Session session = new Session();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
    }
}
