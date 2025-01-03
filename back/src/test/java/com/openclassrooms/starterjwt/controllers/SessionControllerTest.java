package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_ValidId_ReturnsOk() {
        Session session = new Session();
        SessionDto sessionDto = new SessionDto();
        
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void testFindById_InvalidId_ReturnsBadRequest() {
        ResponseEntity<?> response = sessionController.findById("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testFindById_NotFound_ReturnsNotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFindAll_ReturnsOk() {
        List<Session> sessions = Collections.singletonList(new Session());
        List<SessionDto> sessionDtos = Collections.singletonList(new SessionDto());

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionDtos, response.getBody());
    }

    @Test
    void testCreate_ReturnsOk() {
        SessionDto sessionDto = new SessionDto();
        Session session = new Session();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void testUpdate_ValidId_ReturnsOk() {
        SessionDto sessionDto = new SessionDto();
        Session session = new Session();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionDto, response.getBody());
    }

    @Test
    void testUpdate_InvalidId_ReturnsBadRequest() {
        SessionDto sessionDto = new SessionDto();

        ResponseEntity<?> response = sessionController.update("abc", sessionDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSave_ValidId_ReturnsOk() {
        Session session = new Session();

        when(sessionService.getById(1L)).thenReturn(session);
        doNothing().when(sessionService).delete(1L);

        ResponseEntity<?> response = sessionController.save("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSave_InvalidId_ReturnsBadRequest() {
        ResponseEntity<?> response = sessionController.save("abc");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testParticipate_ValidIds_ReturnsOk() {
        doNothing().when(sessionService).participate(1L, 2L);

        ResponseEntity<?> response = sessionController.participate("1", "2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testParticipate_InvalidIds_ReturnsBadRequest() {
        ResponseEntity<?> response = sessionController.participate("abc", "def");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testNoLongerParticipate_ValidIds_ReturnsOk() {
        doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testNoLongerParticipate_InvalidIds_ReturnsBadRequest() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("abc", "def");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
  

    
    
}
