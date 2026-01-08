package es.us.dp1.l6_3_24_25.Petris.lobby.controlador;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.us.dp1.l6_3_24_25.Petris.lobby.interfaz.Lobby;
import es.us.dp1.l6_3_24_25.Petris.lobby.servicio.SalaService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

@WebMvcTest(controllers = SalaController.class)
@Epic("Lobby controller")
@Feature("SalaController endpoints")
@WithMockUser
class SalaControllerTest {

    private static final String BASE_URL = "/api/salas";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaService salaService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("Create lobby returns 200 and body")
    @Description("Verifies that creating a lobby returns OK with lobby data")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void createLobby_returnsLobby() throws Exception {
        Lobby lobby = new Lobby("ABC12345");
        lobby.getJugadores().add("host");
        when(salaService.createLobby()).thenReturn(lobby);

        mockMvc.perform(post(BASE_URL).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigoDeUnion").value("ABC12345"))
            .andExpect(jsonPath("$.jugadores[0]").value("host"));
    }

    @Test
    @DisplayName("Join lobby sends update and returns lobby")
    @Description("Ensures joinLobby publishes update and returns lobby when found")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void joinLobby_returnsLobbyAndSendsMessage() throws Exception {
        String codigo = "ABC12345";
        String player = "Player1";
        Lobby lobby = new Lobby(codigo);
        lobby.getJugadores().add("host");
        lobby.getJugadores().add(player);

        when(salaService.addPlayer(codigo, player)).thenReturn(lobby);
        doNothing().when(messagingTemplate).convertAndSend("/topic/sala/" + codigo, lobby.getJugadores());

        mockMvc.perform(post(BASE_URL + "/{codigo}/unirse", codigo)
            .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content(player))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codigoDeUnion").value(codigo))
            .andExpect(jsonPath("$.jugadores[1]").value(player));

        verify(messagingTemplate).convertAndSend("/topic/sala/" + codigo, lobby.getJugadores());
    }

    @Test
    @DisplayName("Join lobby returns 404 when not found")
    @Description("Returns 404 when trying to join a non-existing lobby")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void joinLobby_notFound() throws Exception {
        String codigo = "MISSING";
        String player = "PlayerX";

        when(salaService.addPlayer(codigo, player)).thenReturn(null);

        mockMvc.perform(post(BASE_URL + "/{codigo}/unirse", codigo)
            .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content(player))
            .andExpect(status().isNotFound());

        verifyNoInteractions(messagingTemplate);
    }
}
