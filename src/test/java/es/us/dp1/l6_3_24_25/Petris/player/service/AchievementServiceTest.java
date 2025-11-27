package es.us.dp1.l6_3_24_25.Petris.player.service;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.match.service.MatchService;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Epic("Achievement module")
class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get all achievements")
    @Description("This method received all the game's achievements")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAllAchievements() {
        List<Achievement> achievementList = achievementService.getAllAchievements();
        assertEquals(3, achievementList.size(), "Incorrect number of achievements");
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achievement by ID")
    @Description("This method receive an achievement by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByCorrectId() {
        Integer id = 1;
        Achievement achievement = achievementService.getAchievementById(id);
        assertEquals(id, achievement.getId(), "Ids don't match");
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achievement by wrong ID")
    @Description("This method receive an achievement by a wrong id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByWrongId() {
        Integer id = 7;
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achivement by name")
    @Description("This method check if the system can recieve an achievement found by name")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByName() {
        String name = "Sarcine Creator";
        Achievement achievement = achievementService.getAchievementByName(name);
        assertEquals(name, achievement.getName(), "Names don't match");
    }

    @Test
    @Feature("Achievement getters")
    @DisplayName("Get achivement by a wrong name")
    @Description("This method check if the system can managed a not existing achievemnt by a name")
    @Severity(SeverityLevel.NORMAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementWrongByName() {
        String name = "Master";
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementByName(name));
    }

    @Test
    @Feature("Save achievement")
    @DisplayName("Save a new achievement")
    @Description("This method check if the system can save a new achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    @Transactional
    void testSaveAchievement() {
        Achievement newAchievement = new Achievement();
        String name = "Ganador";
        String description = "Gana 50 partidas";
        Integer valor = 50;
        String statisticName = "games_won";
        String image = "imagelin.png";

        newAchievement.setName(name);
        newAchievement.setDescription(description);
        newAchievement.setValor(valor);
        newAchievement.setStatisticName(statisticName);
        newAchievement.setImage(image);

        Achievement savedAchievement = achievementService.saveAchievement(newAchievement);
        assertEquals(name, savedAchievement.getName(), "Names don't match");
        assertEquals(description, savedAchievement.getDescription(), "Descriptions don't match");
        assertEquals(valor, savedAchievement.getValor(), "Values don't match");
        assertEquals(statisticName, savedAchievement.getStatisticName(), "Statistic names don't match");
        assertEquals(image, savedAchievement.getImage(), "Images don't match");

    }

    @Test
    @Feature("Delete achievement")
    @DisplayName("Delete an achievement")
    @Description("This method check if the system can delete an achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    @Transactional
    void testDeleteAchievement() {
        Integer id = 3;
        assertTrue(achievementService.getAchievementById(id)!= null, "Achievement to delete not found");
        achievementService.deleteAchievement(id);
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }

    @Test
    @Feature("Delete achievement")
    @DisplayName("Delete an achievement not found")
    @Description("This method check if the system can manage the deletion of a not existing achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("dlozaco")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testDeleteAchievementNotFound() {
        Integer id = 99;
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }
}
