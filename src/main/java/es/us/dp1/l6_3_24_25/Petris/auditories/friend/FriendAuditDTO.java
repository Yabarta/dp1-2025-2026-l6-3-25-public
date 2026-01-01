package es.us.dp1.l6_3_24_25.Petris.auditories.friend;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

@NoArgsConstructor
@Data
public class FriendAuditDTO {
    private Integer id;
    private Integer rev;
    private String revType; // Lo convertiremos de 0,1,2 a texto para que sea legible
    private Date fecha;     // Convertiremos el timestamp a fecha real
    private String receivedBy;
    private String requestedBy;

    public FriendAuditDTO(Integer id, Integer rev, Integer revTypeNum, Long timestamp, String receivedBy, String requestedBy) {
        this.id = id;
        this.rev = rev;
        this.fecha = new Date(timestamp);
        this.receivedBy = receivedBy;
        this.requestedBy = requestedBy;

        switch (revTypeNum) {
            case 0 -> this.revType = "CREADO";
            case 1 -> this.revType = "MODIFICADO";
            case 2 -> this.revType = "ELIMINADO";
            default -> this.revType = "DESCONOCIDO";
        }
    }
}
