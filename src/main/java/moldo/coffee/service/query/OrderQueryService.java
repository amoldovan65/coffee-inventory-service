package moldo.coffee.service.query;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import moldo.coffee.domain.Order;
import moldo.coffee.domain.OrderItem;
import moldo.coffee.domain.OrderStatus;
import moldo.coffee.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional(Transactional.TxType.MANDATORY)
public class OrderQueryService {

    private final EntityManager em;

    public Order getById(final Integer id) {
        return Optional.ofNullable(em.find(Order.class, id))
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public List<Order> getAllByClientId(final Integer clientId) {
        return em.createNamedQuery(Order.GET_ALL_BY_CLIENT_ID, Order.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    public List<Order> getAllByClientIdAndStatus(final Integer clientId, final OrderStatus status) {
        return em.createNamedQuery(Order.GET_ALL_BY_CLIENT_ID_AND_STATUS, Order.class)
                .setParameter("clientId", clientId)
                .setParameter("status", status)
                .getResultList();
    }

    public List<OrderItem> getItems(final List<Integer> orderIds) {
        return em.createNamedQuery(OrderItem.GET_ALL_BY_ORDER_IDS, OrderItem.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
    }

    public void deleteOrderItemsForInventory(final Integer inventoryId) {
        final long count = em.createNamedQuery(OrderItem.COUNT_BY_INVENTORY_ID_AND_ORDER_STATUS, Long.class)
                .setParameter("inventoryId", inventoryId)
                .setParameter("status", OrderStatus.IN_PROGRESS.name())
                .getSingleResult();
        if (count > 0) {
            throw new ValidationException("There are still orders which are in progress for these beans. Make sure the orders are finalized first");
        }

        em.createNamedQuery(OrderItem.DELETE_BY_INVENTORY_ID)
                .setParameter("inventoryId", inventoryId)
                .executeUpdate();
    }

    public void deleteOrderItemsForOrders(final List<Integer> orderIds) {
        em.createNamedQuery(OrderItem.DELETE_BY_ORDER_IDS)
                .setParameter("orderIds", orderIds)
                .executeUpdate();
    }

}
