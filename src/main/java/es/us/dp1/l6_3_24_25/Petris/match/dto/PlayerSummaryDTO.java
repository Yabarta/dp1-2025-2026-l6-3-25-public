package es.us.dp1.l6_3_24_25.Petris.match.dto;

import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import lombok.Data;

@Data
public class PlayerSummaryDTO {
    private Integer id;
    private String nickname;
    private String username;

    public static PlayerSummaryDTO toPlayerSummary(Player player) {
        if (player == null) {
            return null;
        }
        PlayerSummaryDTO dto = new PlayerSummaryDTO();
        dto.setId(player.getId());
        dto.setNickname(player.getNickname());
        dto.setUsername(player.getUser() != null ? player.getUser().getUsername() : null);
        return dto;
    }
}