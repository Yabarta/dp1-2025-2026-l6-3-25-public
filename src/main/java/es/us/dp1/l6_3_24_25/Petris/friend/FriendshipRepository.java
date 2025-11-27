package es.us.dp1.l6_3_24_25.Petris.friend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;

public interface FriendshipRepository extends JpaRepository<Friend, Long> {
    // Buscar una amistad específica entre dos personas
    Optional<Friend> findByRequesterAndReceiver(Player requester, Player receiver);
    
    // Encontrar solicitudes pendientes para un usuario
    List<Friend> findByReceiverAndStatus(Player receiver, FriendshipStatus status);
    
    // Encontrar amigos aceptados (complicado porque el user puede ser requester o receiver)
    @Query("SELECT f FROM Friend f WHERE (f.requester = :player OR f.receiver = :player) AND f.status = 'ACCEPTED'")
    List<Friend> findAcceptedFriendships(@Param("player") Player player);

    @Query("SELECT f FROM Friend f WHERE (f.receiver = :player) AND f.status = 'PENDING'")
    List<Friend> findRequestByPlayer(@Param("player") Player player);
}