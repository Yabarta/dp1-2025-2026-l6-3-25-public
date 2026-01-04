package es.us.dp1.l6_3_24_25.Petris.auditories.friend;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;

import es.us.dp1.l6_3_24_25.Petris.friend.Friend;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FriendAudService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<FriendAuditDTO> verHistorial(Integer playerId){
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Object []> resultados = reader.createQuery()
            .forRevisionsOfEntity(Friend.class, false, true)
            .add(AuditEntity.id().eq(playerId)) // Filtro por ID (equivalente al WHERE)
            .addOrder(AuditEntity.revisionNumber().desc()) // Ordenar
            .getResultList();

        List<FriendAuditDTO> listaDto = new ArrayList<>();

        for (Object[] fila : resultados) {
            // 1. La entidad Friend en ese momento
            Friend friend = (Friend) fila[0];

            // 2. La entidad de Revisión (Donde están los metadatos)
            // IMPORTANTE: Debes tener una clase java 'FriendRevEntity' mapeada con @RevisionEntity
            FriendRevEntity revEntity = (FriendRevEntity) fila[1];

            // 3. El tipo de cambio (ADD, MOD, DEL)
            RevisionType revType = (RevisionType) fila[2];
            // Usamos friend.getId() (o el del parámetro), revEntity.getId(), etc.
            listaDto.add(new FriendAuditDTO(
                friend.getId(),
                revEntity.getId(),
                revType.ordinal(),
                revEntity.getTimestamp(),
                friend.getReceiver().getNickname(),
                friend.getRequester().getNickname()
            ));
        }
        if(listaDto.isEmpty()){
            return null;
        }
        return listaDto;
    }
}
