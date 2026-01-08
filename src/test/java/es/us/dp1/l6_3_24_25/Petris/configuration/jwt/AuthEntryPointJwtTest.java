package es.us.dp1.l6_3_24_25.Petris.configuration.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

@Epic("Security module")
@Feature("Authentication entry point")
@DisplayName("AuthEntryPointJwt Tests")
class AuthEntryPointJwtTest {

	private final AuthEntryPointJwt authEntryPointJwt = new AuthEntryPointJwt();

	@Test
	@DisplayName("Should build 401 JSON response")
	@Description("Ensures commence writes status, content type and error payload")
	@Story("Unauthorized response body")
	@Owner("DiegoVicenteCamara(RXW1249)")
	void testCommenceBuildsUnauthorizedResponse() throws IOException, ServletException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/api/test/path");

		MockHttpServletResponse response = new MockHttpServletResponse();

		AuthenticationException authException = mock(AuthenticationException.class);
		when(authException.getMessage()).thenReturn("Invalid credentials");

		authEntryPointJwt.commence(request, response, authException);

		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

		Map<String, Object> payload = parseResponse(response);
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, payload.get("status"));
		assertEquals("Unauthorized", payload.get("error"));
		assertEquals("Invalid credentials", payload.get("message"));
		assertEquals("/api/test/path", payload.get("path"));
	}

	@Test
	@DisplayName("Should include servlet path even without message")
	@Description("Commence should not fail if exception message is null")
	@Story("Handle null message")
	@Owner("DiegoVicenteCamara(RXW1249)")
	void testCommenceHandlesNullMessage() throws IOException, ServletException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/public/resource");

		MockHttpServletResponse response = new MockHttpServletResponse();

		AuthenticationException authException = mock(AuthenticationException.class);
		when(authException.getMessage()).thenReturn(null);

		authEntryPointJwt.commence(request, response, authException);

		Map<String, Object> payload = parseResponse(response);
		assertEquals("/public/resource", payload.get("path"));
		assertTrue(payload.containsKey("message"));
	}

	private Map<String, Object> parseResponse(MockHttpServletResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(response.getContentAsByteArray(), new TypeReference<Map<String, Object>>() {});
	}
}