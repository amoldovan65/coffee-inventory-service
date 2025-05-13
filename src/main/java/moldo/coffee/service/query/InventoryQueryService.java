package moldo.coffee.service.query;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import moldo.coffee.domain.Inventory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional(Transactional.TxType.MANDATORY)
public class InventoryQueryService {

    private final EntityManager em;

    public Optional<Inventory> findById(final Integer id) {
        return Optional.ofNullable(em.find(Inventory.class, id));
    }

    public Inventory getByIdForUpdate(final Integer id) {
        return Optional.ofNullable(em.find(Inventory.class, id, LockModeType.PESSIMISTIC_WRITE))
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
    }

    public List<Inventory> getAll(final Collection<Integer> ids, final boolean forUpdate) {
        final TypedQuery<Inventory> query = em.createNamedQuery(Inventory.FIND_ALL_BY_ID, Inventory.class)
                .setParameter("ids", ids);
        if (forUpdate) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        return query.getResultList();
    }

}
