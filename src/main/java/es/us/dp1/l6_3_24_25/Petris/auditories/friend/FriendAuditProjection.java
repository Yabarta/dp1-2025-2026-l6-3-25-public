package es.us.dp1.l6_3_24_25.Petris.auditories.friend;

public interface FriendAuditProjection {
    Long getId();
    Integer getRev();
    Integer getRevtype();
    Long getTimestamp();
    String getReceivedBy();
    String getRequestedBy();
}
