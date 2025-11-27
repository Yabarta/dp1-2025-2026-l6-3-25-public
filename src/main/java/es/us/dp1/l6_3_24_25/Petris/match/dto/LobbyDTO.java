package es.us.dp1.l6_3_24_25.Petris.match.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.us.dp1.l6_3_24_25.Petris.match.model.Match;
import lombok.Data;

@Data
public class LobbyDTO {
    private Integer id;
    private String code;
    @JsonProperty("isPrivate")
    private boolean isPrivate;
    private Integer creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private List<PlayerSummaryDTO> players = new ArrayList<>();

    public static LobbyDTO toLobbyDTO(@NonNull Match match) {
        LobbyDTO dto = new LobbyDTO();
        dto.setId(match.getId());
        dto.setCode(match.getCode());
        dto.setPrivate(match.getCode() != null);
        dto.setCreatorId(match.getCreator() != null ? match.getCreator().getId() : null);
        dto.setCreatedAt(match.getCreatedAt());
        dto.setStartedAt(match.getStartedAt());
        dto.setPlayers(buildPlayerList(match));
        return dto;
    }

    private static List<PlayerSummaryDTO> buildPlayerList(Match match) {
        List<PlayerSummaryDTO> players = new ArrayList<>();
        if (match.getPlayer1() != null) {
            players.add(PlayerSummaryDTO.toPlayerSummary(match.getPlayer1()));
        }
        if (match.getPlayer2() != null) {
            players.add(PlayerSummaryDTO.toPlayerSummary(match.getPlayer2()));
        }
        return players;
    }
}
