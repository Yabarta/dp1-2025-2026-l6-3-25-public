package es.us.dp1.l6_3_24_25.Petris.player.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.repository.PlayerRepository;
import jakarta.validation.Valid;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<Player> getAllPlayer() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player getPlayerId(Integer id) {
        return playerRepository.getById(id);
    }

    @Transactional(readOnly = true)
    public Player getByUsername(String username) {
        return playerRepository.getByUsername(username);
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
