package es.us.dp1.l6_3_24_25.Petris.player.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.user.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Issue;

@SpringBootTest
@Epic("Achievement Service")
class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    @Feature("HU-27: Ver logros (jugador)")
    @DisplayName("Get all achievements")
    @Description("This method received all the game's achievements")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAllAchievements() {
        List<Achievement> achievementList = achievementService.getAllAchievements();
        assertEquals(10, achievementList.size(), "Incorrect number of achievements");
    }

    @Test
    @Feature("HU-27: Ver logros (jugador)")
    @DisplayName("Get achievement by ID")
    @Description("This method receive an achievement by a correct id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByCorrectId() {
        Integer id = 1;
        Achievement achievement = achievementService.getAchievementById(id);
        assertEquals(id, achievement.getId(), "Ids don't match");
    }

    @Test
    @Feature("HU-27: Ver logros (jugador)")
    @DisplayName("Get achievement by wrong ID")
    @Description("This method receive an achievement by a wrong id")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByWrongId() {
        Integer id = 689;
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }

    @Test
    @Feature("HU-27: Ver logros (jugador)")
    @DisplayName("Get achivement by name")
    @Description("This method check if the system can recieve an achievement found by name")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementByName() {
        String name = "Creador de sarcinas";
        Achievement achievement = achievementService.getAchievementByName(name);
        assertEquals(name, achievement.getName(), "Names don't match");
    }

    @Test
    @Feature("HU-27: Ver logros (jugador)")
    @DisplayName("Get achivement by a wrong name")
    @Description("This method check if the system can managed a not existing achievemnt by a name")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testGetAchievementWrongByName() {
        String name = "Master";
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementByName(name));
    }

    @Test
    @Feature("HU-32: Definir nuevos logros (administrador)")
    @DisplayName("Save a new achievement")
    @Description("This method check if the system can save a new achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
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
    @Feature("HU-34: Eliminar logros (administrador)")
    @DisplayName("Delete an achievement")
    @Description("This method check if the system can delete an achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    @Transactional
    void testDeleteAchievement() {
        Integer id = 3;
        assertTrue(achievementService.getAchievementById(id)!= null, "Achievement to delete not found");
        achievementService.deleteAchievement(id);
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }

    @Test
    @Feature("HU-34: Eliminar logros (administrador)")
    @DisplayName("Delete an achievement not found")
    @Description("This method check if the system can manage the deletion of a not existing achievement")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Issue("https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/issues/107")
    void testDeleteAchievementNotFound() {
        Integer id = 99;
        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(id));
    }

    @Test
    @Feature("HU-34: Eliminar logros (administrador)")
    @DisplayName("Delete achievement removes it from players")
    @Description("deleteAchievement should remove the achievement from each player and delete it")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testDeleteAchievementRemovesFromPlayers() {
        Achievement achievement = new Achievement();
        achievement.setName("ToRemove" + UUID.randomUUID());
        achievement.setDescription("temp");
        achievement.setValor(1);
        achievement.setStatisticName("stat");
        achievement.setImage("/uploads/temp.png");

        Achievement savedAchievement = achievementService.saveAchievement(achievement);

        Player player = new Player();
        player.setNickname("p-" + UUID.randomUUID());
        player.setEmail("email" + UUID.randomUUID() + "@test.com");
        player.setIsCurrentlyInMatch(false);
        player.setUser(userService.findUser(14));
        player.setStatistics(statisticsService.getStatisticsById(11));
        player.getAchievements().add(savedAchievement);
        Player savedPlayer = playerService.save(player);

        achievementService.deleteAchievement(savedAchievement.getId());

        assertThrows(ResourceNotFoundException.class, () -> achievementService.getAchievementById(savedAchievement.getId()));
        Player reloaded = playerService.getPlayerById(savedPlayer.getId());
        assertFalse(reloaded.getAchievements().contains(savedAchievement));
    }

    @Test
    @Feature("HU-32: Definir nuevos logros (administrador)")
    @DisplayName("Create achievement with image uploads file")
    @Description("createAchievementWithImage should store file on disk and persist path")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testCreateAchievementWithImage() throws IOException {
        byte[] content = "fake-image".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", content);

        Achievement created = achievementService.createAchievementWithImage("New name", "desc", 5, "games_played", file);

        assertNotNull(created.getId());
        assertNotNull(created.getImage());
        Path stored = Paths.get(created.getImage().replaceFirst("/", ""));
        assertTrue(Files.exists(stored), "Uploaded file should exist on disk");

        Files.deleteIfExists(stored);
    }

    @Test
    @Feature("HU-32: Definir nuevos logros (administrador)")
    @DisplayName("Create achievement creates uploads directory when missing")
    @Description("saveUploadedFile should create uploads folder if it does not exist")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testCreateAchievementCreatesUploadsDirectory() throws IOException {
        Path uploadsDir = Paths.get("uploads");
        Path backupDir = uploadsDir.resolveSibling("uploads_backup_" + UUID.randomUUID());
        boolean uploadsExisted = Files.exists(uploadsDir);

        if (uploadsExisted) {
            Files.move(uploadsDir, backupDir);
        }

        try {
            assertFalse(Files.exists(uploadsDir), "uploads dir should not exist before test");

            MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "img".getBytes());

            Achievement created = achievementService.createAchievementWithImage("DirTest", "d", 1, "s", file);

            assertNotNull(created.getImage());
            assertTrue(Files.exists(uploadsDir), "uploads dir should be created");

            Path stored = Paths.get(created.getImage().replaceFirst("/", ""));
            Files.deleteIfExists(stored);
        } finally {
            if (uploadsExisted) {
                deleteDirectoryRecursively(uploadsDir);
                Files.move(backupDir, uploadsDir);
            } else {
                deleteDirectoryRecursively(uploadsDir);
            }
        }
    }

    @Test
    @Feature("HU-33: Editar logros (administrador))")
    @DisplayName("Update achievement replaces image and deletes old")
    @Description("updateAchievementWithImage should delete previous image and save new one")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testUpdateAchievementWithImageDeletesOld() throws IOException {
        Achievement achievement = new Achievement();
        achievement.setName("Temp");
        achievement.setDescription("desc");
        achievement.setValor(1);
        achievement.setStatisticName("stat");

        Path uploadsDir = Paths.get("uploads");
        Files.createDirectories(uploadsDir);
        String oldFileName = UUID.randomUUID() + "_old.png";
        Path oldPath = uploadsDir.resolve(oldFileName);
        Files.write(oldPath, "old".getBytes());
        achievement.setImage("/uploads/" + oldFileName);

        Achievement saved = achievementService.saveAchievement(achievement);

        MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "image/png", "new".getBytes());

        Achievement updated = achievementService.updateAchievementWithImage(saved.getId(), newFile, "Updated", "ndesc", 2, "stat2");

        assertEquals("Updated", updated.getName());
        assertEquals(Integer.valueOf(2), updated.getValor());
        assertTrue(updated.getImage().startsWith("/uploads/"));
        assertFalse(Files.exists(oldPath), "Old image should be removed");

        Path newStored = Paths.get(updated.getImage().replaceFirst("/", ""));
        Files.deleteIfExists(newStored);
    }

    @Test
    @Feature("HU-33: Editar logros (administrador))")
    @DisplayName("Update achievement without image keeps current image")
    @Description("updateAchievementWithImage should preserve image when no file provided")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testUpdateAchievementWithoutImageKeepsExisting() {
        Achievement achievement = new Achievement();
        achievement.setName("KeepImage");
        achievement.setDescription("desc");
        achievement.setValor(1);
        achievement.setStatisticName("stat");
        achievement.setImage("/uploads/existing.png");

        Achievement saved = achievementService.saveAchievement(achievement);

        Achievement updated = achievementService.updateAchievementWithImage(saved.getId(), null, "KeepImage2", null, null, null);

        assertEquals("KeepImage2", updated.getName());
        assertEquals("/uploads/existing.png", updated.getImage());
    }

    @Test
    @Feature("HU-33: Editar logros (administrador))")
    @DisplayName("Update achievement throws when id not found")
    @Description("updateAchievementWithImage should raise ResourceNotFoundException for missing achievement")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testUpdateAchievementWithImageNotFound() {
        MockMultipartFile file = new MockMultipartFile("file", "new.png", "image/png", "new".getBytes());

        assertThrows(ResourceNotFoundException.class, () ->
                achievementService.updateAchievementWithImage(99999, file, "n", "d", 1, "s"));
    }

    @Test
    @Feature("HU-34: Eliminar logros (administrador)")
    @DisplayName("Delete achievement throws when id not found")
    @Description("deleteAchievement should raise ResourceNotFoundException when the achievement does not exist")
    @Severity(SeverityLevel.NORMAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testDeleteAchievementThrowsWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> achievementService.deleteAchievement(88888));
    }

    @Test
    @Feature("HU-32: Definir nuevos logros (administrador)")
    @DisplayName("Create achievement with image failure surfaces runtime exception")
    @Description("createAchievementWithImage should wrap IO issues into RuntimeException")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCreateAchievementWithImageIoFailure() throws IOException {
        MultipartFile failingFile = mock(MultipartFile.class);
        when(failingFile.isEmpty()).thenReturn(false);
        when(failingFile.getOriginalFilename()).thenReturn("fail.png");
        when(failingFile.getInputStream()).thenThrow(new IOException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                achievementService.createAchievementWithImage("n", "d", 1, "s", failingFile));

        assertTrue(ex.getMessage().contains("Error al guardar la imagen"));
    }

    @Test
    @Feature("HU-32: Definir nuevos logros (administrador)")
    @DisplayName("Update achievement with image failure surfaces runtime exception")
    @Description("updateAchievementWithImage should wrap IO issues into RuntimeException")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("DiegoVicenteCamara(RXW1249)")
    @Transactional
    void testUpdateAchievementWithImageIoFailure() throws IOException {
        Achievement achievement = new Achievement();
        achievement.setName("WithImage");
        achievement.setDescription("d");
        achievement.setValor(1);
        achievement.setStatisticName("stat");
        achievement.setImage("/uploads/existing.png");
        Achievement saved = achievementService.saveAchievement(achievement);

        MultipartFile failingFile = mock(MultipartFile.class);
        when(failingFile.isEmpty()).thenReturn(false);
        when(failingFile.getOriginalFilename()).thenReturn("fail.png");
        when(failingFile.getInputStream()).thenThrow(new IOException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                achievementService.updateAchievementWithImage(saved.getId(), failingFile, "new", "nd", 2, "stat2"));

        assertTrue(ex.getMessage().contains("Error al guardar la imagen"));
    }

    private void deleteDirectoryRecursively(Path dir) throws IOException {
        if (dir != null && Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
