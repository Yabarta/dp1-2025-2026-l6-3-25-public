package es.us.dp1.l6_3_24_25.Petris.configuration.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import es.us.dp1.l6_3_24_25.Petris.configuration.services.UserDetailsImpl;
import es.us.dp1.l6_3_24_25.Petris.user.Authorities;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Security module")
@Feature("JWT Utilities")
@SpringBootTest
@TestPropertySource(properties = {
    "dp1.game.app.jwtSecret=testSecretKey1234567890testSecretKey1234567890testSecretKey123456",
    "dp1.game.app.jwtExpirationMs=86400000"
})
@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private Authentication authentication;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("PLAYER"));
        
        userDetails = new UserDetailsImpl(1, "testuser", "password", authorities);

        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Test
    @DisplayName("Should generate JWT token from authentication")
    @Description("Test that a JWT token is correctly generated from an Authentication object")
    @Story("Generate JWT token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGenerateJwtToken_Success() {
        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Should extract username from JWT token")
    @Description("Test that username can be correctly extracted from a JWT token")
    @Story("Extract username from token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetUserNameFromJwtToken_Success() {
        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should validate correct JWT token")
    @Description("Test that a valid JWT token is recognized as valid")
    @Story("Validate JWT token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Valid() {
        String token = jwtUtils.generateJwtToken(authentication);
        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid JWT token")
    @Description("Test that an invalid JWT token is rejected")
    @Story("Reject invalid token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Invalid() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject malformed JWT token")
    @Description("Test that a malformed JWT token is rejected")
    @Story("Reject malformed token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Malformed() {
        String malformedToken = "this.is.notavalidtoken";
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate token from username and authority")
    @Description("Test that a JWT token can be generated from username and authority")
    @Story("Generate token from username")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGenerateTokenFromUsername_Success() {
        Authorities authority = new Authorities();
        authority.setAuthority("PLAYER");

        String token = jwtUtils.generateTokenFromUsername("newuser", authority);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("newuser", username);
    }

    @Test
    @DisplayName("Should handle different authorities")
    @Description("Test that different authorities can be encoded in JWT")
    @Story("Handle different authorities")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGenerateTokenFromUsername_DifferentAuthorities() {
        Authorities authority = new Authorities();
        authority.setAuthority("ADMIN");

        String token = jwtUtils.generateTokenFromUsername("admin", authority);

        assertNotNull(token);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("admin", username);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    @Description("Test that different tokens are generated for different users")
    @Story("Different tokens for different users")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGenerateJwtToken_DifferentUsers() {
        String token1 = jwtUtils.generateJwtToken(authentication);
        
        // Create another authentication
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        
        UserDetailsImpl userDetails2 = new UserDetailsImpl(2, "otheruser", "password", authorities);

        Authentication authentication2 = new UsernamePasswordAuthenticationToken(userDetails2, null, authorities);
        String token2 = jwtUtils.generateJwtToken(authentication2);

        assertNotEquals(token1, token2);
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token1));
        assertEquals("otheruser", jwtUtils.getUserNameFromJwtToken(token2));
    }

    @Test
    @DisplayName("Should handle null token in validation")
    @Description("Test that null token is handled in validation")
    @Story("Handle null token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Null() {
        boolean isValid = jwtUtils.validateJwtToken(null);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle empty token in validation")
    @Description("Test that empty token is handled in validation")
    @Story("Handle empty token")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Empty() {
        boolean isValid = jwtUtils.validateJwtToken("");
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token should contain username claim")
    @Description("Test that generated token contains username in claims")
    @Story("Verify token claims")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGenerateJwtToken_ContainsUsername() {
        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertNotNull(username);
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should reject token with wrong signature")
    @Description("Validate should return false when token signature does not match configured secret")
    @Story("Invalid signature handling")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_InvalidSignature() {
        String otherSecret = "otherSecretKey1234567890otherSecretKey1234567890otherSecretKey123456";
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS512, otherSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired JWT token")
    @Description("Validate should catch expired tokens")
    @Story("Expired token handling")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Expired() {
        String secret = (String) ReflectionTestUtils.getField(jwtUtils, "jwtSecret");

        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 120000))
                .setExpiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject unsupported JWT token")
    @Description("Validate should catch tokens without signature as unsupported")
    @Story("Unsupported token handling")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_Unsupported() {
        String unsignedToken = Jwts.builder().setSubject("user-without-signature").compact();

        boolean isValid = jwtUtils.validateJwtToken(unsignedToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject token with empty claims")
    @Description("Validate should catch illegal argument cases explicitly")
    @Story("Illegal argument handling")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testValidateJwtToken_IllegalArgument() {
        boolean isValid = jwtUtils.validateJwtToken(" ");

        assertFalse(isValid);
    }
}