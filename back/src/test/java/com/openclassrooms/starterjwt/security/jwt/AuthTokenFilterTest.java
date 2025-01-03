package com.openclassrooms.starterjwt.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext(); // Clear SecurityContext for isolation
    }

    @Test
    void testDoFilterInternal_ValidJwt() throws ServletException, IOException {
        // Arrange
        String jwt = "valid.jwt.token";
        String username = "testUser";

        request.addHeader("Authorization", "Bearer " + jwt);

        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(jwt);
        verify(jwtUtils).getUserNameFromJwtToken(jwt);
        verify(userDetailsService).loadUserByUsername(username);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Ensure the filter chain proceeds
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidJwt() throws ServletException, IOException {
        // Arrange
        String jwt = "invalid.jwt.token";

        request.addHeader("Authorization", "Bearer " + jwt);

        when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(jwt);
        verifyNoInteractions(userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoJwt() throws ServletException, IOException {
        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verifyNoInteractions(jwtUtils, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // Arrange
        String jwt = "valid.jwt.token";
        String username = "testUser";

        request.addHeader("Authorization", "Bearer " + jwt);

        when(jwtUtils.validateJwtToken(jwt)).thenThrow(new RuntimeException("Test exception"));

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtUtils).validateJwtToken(jwt);

        // Ensure no authentication is set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain).doFilter(request, response);
    }
}
