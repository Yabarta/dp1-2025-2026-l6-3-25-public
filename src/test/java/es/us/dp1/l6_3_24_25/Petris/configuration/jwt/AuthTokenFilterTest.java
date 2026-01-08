package es.us.dp1.l6_3_24_25.Petris.configuration.jwt;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import es.us.dp1.l6_3_24_25.Petris.configuration.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Security module")
@Feature("JWT Authentication Token Filter")
@TestPropertySource(properties = {
    "dp1.game.app.jwtSecret=testSecretKey1234567890testSecretKey1234567890testSecretKey123456",
    "dp1.game.app.jwtExpirationMs=86400000"
})
@DisplayName("AuthTokenFilter Tests")
class AuthTokenFilterTest {

    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authTokenFilter = new AuthTokenFilter();
        
        ReflectionTestUtils.setField(authTokenFilter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authTokenFilter, "userDetailsService", userDetailsService);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should parse JWT from Authorization header")
    @Description("Test that JWT token is correctly parsed from Bearer token in Authorization header")
    @Story("Parse JWT from header")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "validjwttoken";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, times(1)).validateJwtToken(token);
        verify(jwtUtils, times(1)).getUserNameFromJwtToken(token);
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle missing Authorization header")
    @Description("Test that missing Authorization header is handled gracefully")
    @Story("Handle missing header")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle empty Authorization header")
    @Description("Test that empty Authorization header is handled correctly")
    @Story("Handle empty header")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_EmptyAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle invalid token format")
    @Description("Test that tokens without Bearer prefix are ignored")
    @Story("Handle invalid format")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_InvalidTokenFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle invalid JWT token")
    @Description("Test that invalid JWT tokens are not processed")
    @Story("Handle invalid token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "invalidtoken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils, times(1)).validateJwtToken(token);
        verify(jwtUtils, never()).getUserNameFromJwtToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle exception during JWT processing")
    @Description("Test that exceptions during JWT processing are caught and filter continues")
    @Story("Handle exception")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_ExceptionDuringJwtProcessing() throws ServletException, IOException {
        String token = "tokenThatThrowsException";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("JWT processing error"));

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should set authentication in SecurityContext for valid token")
    @Description("Test that valid token results in authentication being set in SecurityContext")
    @Story("Set authentication context")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_SetSecurityContext() throws ServletException, IOException {
        String token = "validtoken";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that authentication was set in security context
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.junit.jupiter.api.Assertions.assertNotNull(authentication, "Authentication should be set in SecurityContext");
        org.junit.jupiter.api.Assertions.assertTrue(authentication instanceof UsernamePasswordAuthenticationToken);
    }

    @Test
    @DisplayName("Should continue filter chain even without authentication")
    @Description("Test that filter chain continues even when token is not present")
    @Story("Continue filter chain")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_ContinuesChainWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should extract token correctly from Bearer format")
    @Description("Test that token is correctly extracted from 'Bearer <token>' format")
    @Story("Extract Bearer token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_ExtractBearerToken() throws ServletException, IOException {
        String token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        authTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUserNameFromJwtToken(token);
    }

    @Test
    @DisplayName("Should handle whitespace in Authorization header")
    @Description("Test that whitespace in Authorization header is handled properly")
    @Story("Handle whitespace")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDoFilterInternal_WhitespaceInHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("  Bearer validtoken  ");

        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Bearer token with spaces should be handled (or ignored depending on implementation)
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
