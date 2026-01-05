package es.us.dp1.l6_3_24_25.Petris.auditories.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/audit/friends")
public class FriendAuditController {

    @Autowired
    private FriendAudService friendAudService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendAuditDTO>> getAuditHistory(@PathVariable("userId") Integer userId) {
        List<FriendAuditDTO> auditHistory = friendAudService.verHistorial(userId);
        return ResponseEntity.ok(auditHistory);
    }
}
