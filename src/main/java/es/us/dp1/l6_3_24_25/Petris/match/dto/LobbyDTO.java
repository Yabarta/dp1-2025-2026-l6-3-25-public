package es.us.dp1.l6_3_24_25.Petris.match.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
