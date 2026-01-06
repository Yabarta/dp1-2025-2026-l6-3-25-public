package es.us.dp1.l6_3_24_25.Petris.player.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Achievement;
import es.us.dp1.l6_3_24_25.Petris.player.repository.AchievementRepository;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;

@Service
public class AchievementService {

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
}
