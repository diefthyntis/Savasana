package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public Session create(Session session) {
        return this.sessionRepository.save(session);
    }

    public void delete(Long id) {
        this.sessionRepository.deleteById(id);
    }

    public List<Session> findAll() {
        return this.sessionRepository.findAll();
    }

    public Session getById(Long id) {
        return this.sessionRepository.findById(id).orElse(null);
    }

    public Session update(Long id, Session session) {
        session.setId(id);
        return this.sessionRepository.save(session);
    }

    public void participate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id).orElse(null);
        User user = this.userRepository.findById(userId).orElse(null);
        
        /*
        if (session == null || user == null) {
            throw new NotFoundException();
        }
        */

        boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
        if(alreadyParticipate) {
            throw new BadRequestException();
        }

        session.getUsers().add(user);

        this.sessionRepository.save(session);
    }

    public void noLongerParticipate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id).orElse(null);
        
        /*
        if (session == null) {
            throw new NotFoundException();
        }
        */

        boolean alreadyParticipate = session.getUsers().stream().anyMatch(o -> o.getId().equals(userId));
        if(!alreadyParticipate) {
            throw new BadRequestException();
        }

        /*
        session.setUsers(session.getUsers().stream().filter(user -> !user.getId().equals(userId)).collect(Collectors.toList()));
		trop compliqué à tester
		*/
        List<User> currentUsers = session.getUsers();
        List<User> updatedUsers = new ArrayList<>();
        for (User user : currentUsers) {
            if (!user.getId().equals(userId)) {
                updatedUsers.add(user);
            }
        }
        /*
        List<User> updatedUsers = currentUsers.stream()
                                           .filter(user -> !user.getId().equals(userId))
                                           .collect(Collectors.toList());
                                           */
        session.setUsers(updatedUsers);
        
        
        
        this.sessionRepository.save(session);
    }
}
