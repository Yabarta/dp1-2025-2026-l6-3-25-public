package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;
import es.us.dp1.l6_3_24_25.Petris.user.User;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player getPlayerByUser(User user) {
        return playerRepository.getByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "user", user));
    }

    @Transactional(readOnly = true)
    public Player getPlayerById(Integer id) {
        return playerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
    }

    @Transactional(readOnly = true)
    public Player getPlayerByNickname(String username) {
        return playerRepository.getByNickname(username)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "username", username));
    }

    @Transactional(readOnly = true)
    public Player getPlayerByUsername(String username) {
        return playerRepository.getByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "username", username));
    }

    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public void delete(Integer id) {
        playerRepository.deleteById(id);
    }

}
