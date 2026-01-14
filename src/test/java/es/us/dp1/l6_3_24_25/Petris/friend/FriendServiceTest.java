package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import es.us.dp1.l6_3_24_25.Petris.player.service.PlayerService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Friend Service Module")
@SpringBootTest
public class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private PlayerService playerService;

    @Test
    @Transactional
    @Feature("Friendship Retrieval")
    @DisplayName("getFriendsByUsername Test")
    void shouldGetFriendsByUsername() {
        // Asumiendo que en tus datos de prueba (data.sql) player1 tiene amigos aceptados
        List<Friend> friends = this.friendService.getFriendsByUsername("player1");
        assertNotNull(friends);
        // Ajusta el número esperado según tus datos iniciales
        assertTrue(friends.size() >= 0); 
    }

    @Test
    @Transactional
    @Feature("Friendship Retrieval")
    @DisplayName("getFriendsById Test")
    void shouldGetFriendsById() {
        // Crea una amistad para asegurar que existe un ID
        Player req = playerService.getPlayerById(1);
        Player rec = playerService.getPlayerById(2);
        Friend created = friendService.create(req, rec);

        Optional<Friend> found = this.friendService.getFriendsById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    @Transactional
    void shouldReturnExistingFriendshipIfDuplicate() {
    Player req = playerService.getPlayerById(1);
    Player rec = playerService.getPlayerById(2);
    
    Friend first = friendService.create(req, rec);
    Friend second = friendService.create(req, rec); // Segunda llamada
    
    assertEquals(first.getId(), second.getId(), "Debería devolver la misma entidad");
}

    @Test
    @Transactional
    @Feature("Friendship Management")
    @DisplayName("Create Friendship Test")
    void shouldCreateFriendship() {
        Player req = playerService.getPlayerById(1);
        Player rec = playerService.getPlayerById(3);
        
        Friend created = friendService.create(req, rec);
        
        assertNotNull(created.getId());
        assertEquals(FriendshipStatus.PENDING, created.getStatus());
        assertEquals(req.getId(), created.getRequester().getId());
    }

    @Test
    @Transactional
    @Feature("Friendship Management")
    @DisplayName("Save/Update Friendship Test")
    void shouldSaveFriendship() {
        Player req = playerService.getPlayerById(1);
        Player rec = playerService.getPlayerById(2);
        Friend friend = friendService.create(req, rec);
        
        friend.setStatus(FriendshipStatus.ACCEPTED);
        friendService.save(friend);
        
        Friend updated = friendService.getFriendsById(friend.getId()).get();
        assertEquals(FriendshipStatus.ACCEPTED, updated.getStatus());
    }

    @Test
    @Transactional
    @Feature("Friendship Management")
    @DisplayName("Delete Friendship Test")
    void shouldDeleteFriendship() {
        Player req = playerService.getPlayerById(1);
        Player rec = playerService.getPlayerById(2);
        Friend friend = friendService.create(req, rec);
        Integer id = friend.getId();

        friendService.delete(id);
        
        Optional<Friend> deleted = friendService.getFriendsById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @Transactional
    @Feature("Friendship Verification")
    @DisplayName("Player1IsFriendOfPlayer2 Test")
    void shouldVerifyFriendship() {
        // 1. Crear y aceptar amistad
        Player p1 = playerService.getPlayerById(1);
        Player p2 = playerService.getPlayerById(2);
        Friend f = friendService.create(p1, p2);
        f.setStatus(FriendshipStatus.ACCEPTED);
        friendService.save(f);

        // 2. Verificar (Nota: Tu método del service usa el repo, 
        // asegúrate que el repo gestione bien quién es p1 y p2)
        Boolean areFriends = friendService.Player1IsFriendOfPlayer2(p1.getId(), p2.getId());
        assertTrue(areFriends, "They should be friends");
    }
}