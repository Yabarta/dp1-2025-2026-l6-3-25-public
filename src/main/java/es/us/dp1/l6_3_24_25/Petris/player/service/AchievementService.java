package es.us.dp1.l6_3_24_25.Petris.player.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.repository.AchievementRepository;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AchievementService {
    private static final String DEFAULT_ACHIEVEMENT_IMAGE = "src/main/resources/static/images/trofeo.png";
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Achievement getAchievementById(Integer id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
    }

    @Transactional(readOnly = true)
    public Achievement getAchievementByName(String name) {
        return achievementRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "name", name));
    }

    @Transactional
    public Achievement saveAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void deleteAchievement(Integer id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
        playerRepository.findAll().forEach(player -> {
            if (player.getAchievements().contains(achievement)) {
                player.getAchievements().remove(achievement);
                playerRepository.save(player);
            }
        });
        achievementRepository.deleteById(id);
    }

    @Transactional
    public Achievement createAchievementWithImage(String name, String description, Integer valor, 
                                                  String statisticName, MultipartFile file) {
        Achievement achievement = new Achievement();
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setValor(valor);
        achievement.setStatisticName(statisticName);

        if (file != null && !file.isEmpty()) {
            try {
                String imagePath = saveUploadedFile(file);
                achievement.setImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
            }
        }else {
            achievement.setImage(DEFAULT_ACHIEVEMENT_IMAGE);
        }

        return achievementRepository.save(achievement);
    }

    @Transactional
    public Achievement updateAchievementWithImage(Integer id, MultipartFile file, String name, 
                                                  String description, Integer valor, String statisticName) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));

        if (name != null) achievement.setName(name);
        if (description != null) achievement.setDescription(description);
        if (valor != null) achievement.setValor(valor);
        if (statisticName != null) achievement.setStatisticName(statisticName);

        if (file != null && !file.isEmpty()) {
            try {
                deleteOldAchievementImage(achievement);
                String imagePath = saveUploadedFile(file);
                achievement.setImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
            }
        }

        return achievementRepository.save(achievement);
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    private void deleteOldAchievementImage(Achievement achievement) throws IOException {
        String oldImage = achievement.getImage();
        if (oldImage != null && !oldImage.isEmpty() && oldImage.startsWith("/uploads/")) {
            String fileName = oldImage.substring("/uploads/".length());
            Path oldFilePath = Paths.get("uploads").resolve(fileName);
            Files.deleteIfExists(oldFilePath);
        }
    }
}
