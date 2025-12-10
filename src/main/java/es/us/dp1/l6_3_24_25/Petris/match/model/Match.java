package es.us.dp1.l6_3_24_25.Petris.match.model;

import es.us.dp1.l6_3_24_25.Petris.match.util.MatchDataUtil;
import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import es.us.dp1.l6_3_24_25.Petris.player.model.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class Match extends BaseEntity{
    @NotNull
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String code;
    @Min(0)
    @Max(MatchDataUtil.MAX_SCORE)
    private int player1Score;
    @Min(0)
    @Max(MatchDataUtil.MAX_SCORE)
    private int player2Score;
    @Min(MatchDataUtil.PLAYER_1_WINS)
    @Max(MatchDataUtil.PLAYER_2_WINS)
    private Integer winner;
    @Min(0)
    private int turn;
    private TurnType turnType;

    @NotNull
    @ManyToOne(optional = false)
    private Player creator;

    @OneToMany(cascade = CascadeType.ALL)
    @Size(min = MatchDataUtil.NUM_PETRI_DISHES, max = MatchDataUtil.NUM_PETRI_DISHES)
    private List<PetriDish> boardState;

    @ManyToOne()
    private Player player1;

    @ManyToOne()
    private Player player2;

    public boolean isValidCode(String code) {
        return this.getCode() == null || this.getCode().equalsIgnoreCase(code);
    }

    public boolean hasPlayer(Player player) {
        return player.equals(this.getPlayer1()) || player.equals(this.getPlayer2());
    }

    public boolean hasPlayer1(Player player) {
        return player.equals(this.getPlayer1());
    }

    public boolean hasPlayer2(Player player) {
        return player.equals(this.getPlayer2());
    }

    public boolean hasCreator(Player player) {
        return this.getCreator().equals(player);
    }

    public boolean hasStarted() {
        return this.getStartedAt() != null;
    }

    public boolean hasEnded() {
        return this.getEndedAt() != null;
    }

    public boolean isFull() {
        return this.player1 != null && this.player2 != null;
    }

    public boolean isTurnOf(Player player) {
        return (player.equals(this.getPlayer1()) && this.getTurnType().equals(TurnType.P1_PROPAGATION)) ||
               (player.equals(this.getPlayer2()) && this.getTurnType().equals(TurnType.P2_PROPAGATION));
    }

    public boolean isInPropagationTurn() {
        return this.getTurnType().equals(TurnType.P1_PROPAGATION) || this.getTurnType().equals(TurnType.P2_PROPAGATION);
    }

    public boolean isPastLastTurn() {
        return this.getTurn() == MatchDataUtil.getTurnsNum();
    }
}
