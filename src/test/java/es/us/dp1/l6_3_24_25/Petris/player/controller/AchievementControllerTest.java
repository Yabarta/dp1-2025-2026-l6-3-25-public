package es.us.dp1.l6_3_24_25.Petris.player.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.service.AchievementService;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AchievementController.class)
@Epic("Achievement controller")
class AchievementControllerTest {

    private static final String BASE_URL = "/api/v1/achievements";
    private static final Integer TEST_ACHIEVEMENT_ID = 1;

    @Autowired
    private AchievementController achievementController;

    @MockBean
    private AchievementService achievementService;

    @Autowired
    private MockMvc mockMvc;

    private Achievement achievement;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        achievement = new Achievement();
        achievement.setId(1);
        achievement.setName("First Win");
        achievement.setDescription("Win your first game.");
        achievement.setValor(1);
        achievement.setStatisticName("games_won");
        achievement.setImage("firstwin.png");
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get all achievements")
    @Description("This method received all the game's achievements")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void getAllAchievements_ReturnAchievementList() throws Exception {
        Achievement firstAchievement = new Achievement();
        firstAchievement.setName("5 in a row!");
        firstAchievement.setDescription("Win 5 games consecutively.");
        firstAchievement.setValor(5);
        firstAchievement.setStatisticName("games_won");
        firstAchievement.setImage("imagelin.png");

        Achievement secondAchievement = new Achievement();
        secondAchievement.setName("10 in a row!");
        secondAchievement.setDescription("Win 10 games consecutively.");
        secondAchievement.setValor(10);
        secondAchievement.setStatisticName("games_won");
        secondAchievement.setImage("imagelin.png");

        when(achievementService.getAllAchievements()).thenReturn(List.of(firstAchievement, secondAchievement));

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].name").value("5 in a row!"))
            .andExpect(jsonPath("$[1].name").value("10 in a row!"));
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achievement by ID")
    @Description("This method receive an achievement by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void getAchievementById_ExistingId_returnStatus200() throws Exception {
        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement);
        mockMvc.perform(get(BASE_URL + "/" + TEST_ACHIEVEMENT_ID)).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_ACHIEVEMENT_ID))
            .andExpect(jsonPath("$.name").value(achievement.getName()))
            .andExpect(jsonPath("$.description").value(achievement.getDescription()));
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achievement by wrong ID")
    @Description("This method throws an exception when try to receive an achievement by a wrong id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void getAchievementById_NotExistingId_returnStatus404() throws Exception {
        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/" + TEST_ACHIEVEMENT_ID)).andExpect(status().isNotFound());
    }

    @Test
    @Feature("Achievement creation")
    @DisplayName("Create achievement")
    @Description("This method creates a new achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void saveAchievement_ValidDataSubmitted_returnStatus201() throws Exception{
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Champion");
        newAchievement.setDescription("Win 100 games.");
        newAchievement.setValor(100);
        newAchievement.setStatisticName("games_won");
        newAchievement.setImage("champion.png");

        given(achievementService.saveAchievement(any(Achievement.class))).willReturn(newAchievement);

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newAchievement))).andExpect(status().isCreated());
    }

    @Test
    @Feature("Achievement update")
    @DisplayName("Update achievement")
    @Description("This method update an achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void updateAchievement_ValidDataSubmitted_ReturnStatus200() throws Exception{
        achievement.setName("Updated Name");
        achievement.setDescription("Updated description");

        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement);
        when(achievementService.saveAchievement(any(Achievement.class))).thenReturn(achievement);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement))).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @Feature("Achievement update")
    @DisplayName("Update achievement with wrong ID")
    @Description("This method throws an exception when try to update an achievement with a wrong id")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void updateAchievement_WrongId_ReturnStatus404() throws Exception{
        achievement.setName("Updated Name");
        achievement.setDescription("Updated description");

        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement))).andExpect(status().isNotFound());
    }


    @Test
    @Feature("Achievement deletion")
    @DisplayName("Delete achievement")
    @Description("This method delete an achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void deleteAchievement_ValidId_ReturnNoContent() throws Exception{
        achievement.setId(2);

        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement);
        doNothing().when(achievementService).deleteAchievement(TEST_ACHIEVEMENT_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()))
            .andExpect(status().isNoContent());

    }

    @Test
    @Feature("Achievement deletion")
    @DisplayName("Delete achievement with wrong ID")
    @Description("This method throws an exception when try to delete an achievement with a wrong id")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/111")
    @WithMockUser("admin")
    void deleteAchievement_WrongId_ReturnStatus404() throws Exception{
        achievement.setId(TEST_ACHIEVEMENT_ID);
        when(achievementService.getAchievementById(TEST_ACHIEVEMENT_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()))
            .andExpect(status().isNotFound());
    }
}
