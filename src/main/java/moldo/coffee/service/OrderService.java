package moldo.coffee.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import moldo.coffee.domain.Order;
import moldo.coffee.domain.OrderItem;
import moldo.coffee.domain.OrderStatus;
import moldo.coffee.exception.ValidationException;
import moldo.coffee.service.query.OrderQueryService;
import moldo.coffee.web.domain.ItemRequest;
import moldo.coffee.web.domain.ItemResult;
import moldo.coffee.web.domain.OrderResult;

import java.util.*;
import java.util.stream.Collectors;

@JBossLog
@Transactional
@ApplicationScoped
@RequiredArgsConstructor
public class OrderService {

    private final EntityManager em;
    private final OrderQueryService orderQueryService;
    private final InventoryService inventoryService;

    public void createOrder(final Integer clientId, final List<ItemRequest> items) {
        log.info(String.format("Creating order: %s", items));

        final Order order = new Order();
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setClientId(clientId);
        em.persist(order);
        em.flush();

        final List<OrderItem> orderItems = createOrderItems(order.getId(), items);
        log.info("Removing quantities for order: " + order.getId());
        inventoryService.removeQuantity(orderItems);
    }

    public void updateOrderStatus(final Integer id, final OrderStatus status) {
        log.info(String.format("Updating status of order (%s) to %s", id, status));

        final Order order = orderQueryService.getById(id);
        order.setStatus(status);
        em.persist(order);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            final List<OrderItem> items = orderQueryService.getItems(List.of(id));
            log.info("Restoring quantities for order: " + id);
            inventoryService.restoreQuantity(items);
        }
    }

    public void deleteOrder(final Integer id) {
        log.info("Deleting order with id " + id);
        final Order order = orderQueryService.getById(id);
        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            throw new ValidationException("Cannot delete an in-progress order");
        }

        orderQueryService.deleteOrderItemsForOrders(List.of(id));
        em.remove(order);
    }

    public void deleteOrdersByClientId(final Integer clientId) {
        log.info("Deleting orders by client id " + clientId);
        final List<Order> orders = orderQueryService.getAllByClientId(clientId);
        if (orders.isEmpty()) {
            return;
        }

        final long inProgressOrders = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.IN_PROGRESS)
                .count();
        if (inProgressOrders > 0) {
            throw new ValidationException("This client still has orders which are in progress");
        }

        final List<Integer> orderIds = orders.stream()
                .map(Order::getId)
                .toList();
        orderQueryService.deleteOrderItemsForOrders(orderIds);
        orders.forEach(em::remove);
    }

    public List<OrderResult> getOrdersOfClient(final Integer clientId) {
        final List<Order> orders = orderQueryService.getAllByClientId(clientId);
        if (orders.isEmpty()) {
            return List.of();
        }

        final List<Integer> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        final List<OrderItem> orderItems = orderQueryService.getItems(orderIds);
        final Map<Integer, List<OrderItem>> itemsByOrder = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId, Collectors.toList()));
        final Map<Integer, Long> costsByOrder = getCostsByOrder(orderItems);

        return orders.stream()
                .map(order -> {
                    final List<OrderItem> items = itemsByOrder.get(order.getId());
                    final List<ItemResult> itemResults = mapItems(items);
                    final Long cost = costsByOrder.getOrDefault(order.getId(), 0L);
                    return new OrderResult(order.getStatus(), cost, itemResults);
                })
                .toList();
    }

    private List<OrderItem> createOrderItems(final Integer orderId, final List<ItemRequest> orderItems) {
        validateOrderItems(orderItems);

        final List<OrderItem> items = new ArrayList<>();
        for (final var item : orderItems) {
            final OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setInventoryId(item.getBeanId());
            orderItem.setQuantity(item.getQuantity());
            em.persist(orderItem);
            items.add(orderItem);
        }
        return items;
    }

    private void validateOrderItems(final List<ItemRequest> orderItems) {
        final Set<Integer> beanIds = orderItems.stream()
                .map(ItemRequest::getBeanId)
                .collect(Collectors.toSet());
        if (beanIds.size() != orderItems.size()) {
            throw new ValidationException("You cannot order the same beans multiple times in a single order");
        }
    }

    private List<ItemResult> mapItems(final List<OrderItem> orderItems) {
        if (orderItems == null) {
            return new ArrayList<>();
        }

        return orderItems.stream()
                .map(item -> new ItemResult(item.getInventoryId(), item.getQuantity()))
                .toList();
    }

    private Map<Integer, Long> getCostsByOrder(final List<OrderItem> orderItems) {
        if (orderItems == null) {
            return new HashMap<>();
        }

        final Set<Integer> inventoryIds = orderItems.stream()
                .map(OrderItem::getInventoryId)
                .collect(Collectors.toSet());
        final Map<Integer, Long> costsByInventory = inventoryService.getCostsByInventory(inventoryIds);
        final Map<Integer, Long> costsByOrder = new HashMap<>();
        for (final var item : orderItems) {
            final Long inventoryCost = costsByInventory.getOrDefault(item.getInventoryId(), 0L);
            final Long orderCost = costsByOrder.getOrDefault(item.getOrderId(), 0L);
            costsByOrder.put(item.getOrderId(), orderCost + inventoryCost * item.getQuantity());
        }

        return costsByOrder;
    }

}
