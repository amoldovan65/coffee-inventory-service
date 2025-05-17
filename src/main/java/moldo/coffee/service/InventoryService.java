package moldo.coffee.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import moldo.coffee.domain.Inventory;
import moldo.coffee.domain.OrderItem;
import moldo.coffee.exception.ValidationException;
import moldo.coffee.service.query.InventoryQueryService;
import moldo.coffee.service.query.OrderQueryService;
import moldo.coffee.web.domain.InventoryCreateRequest;
import moldo.coffee.web.domain.InventoryEditRequest;
import moldo.coffee.web.domain.InventoryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@JBossLog
@Transactional
@ApplicationScoped
@RequiredArgsConstructor
public class InventoryService {

    private final EntityManager em;
    private final InventoryQueryService inventoryQueryService;
    private final OrderQueryService orderQueryService;

    public void createInventory(final InventoryCreateRequest request) {
        log.info("Creating inventory: " + request);
        final Inventory inventory = new Inventory();
        inventory.setId(request.getBeanId());
        inventory.setQuantity(request.getQuantity());
        inventory.setPrice(request.getPrice());
        em.persist(inventory);
    }

    public void updateInventory(final Integer id, final InventoryEditRequest request) {
        log.info(String.format("Updating inventory (%s): %s", id, request));
        final Inventory inventory = inventoryQueryService.getByIdForUpdate(id);
        inventory.setQuantity(request.getQuantity());
        inventory.setPrice(request.getPrice());
        em.persist(inventory);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public void removeQuantity(final List<OrderItem> orderItems) {
        final Map<Integer, Inventory> inventoriesByIds = getInventoriesForOrderItems(orderItems);
        orderItems.forEach(item -> {
            final Inventory inventory = inventoriesByIds.get(item.getInventoryId());
            if (inventory == null) {
                throw new NotFoundException("Inventory not found");
            }
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ValidationException("There is not enough quantity in stock");
            }

            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            em.persist(inventory);
        });
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public void restoreQuantity(final List<OrderItem> orderItems) {
        final Map<Integer, Inventory> inventoriesByIds = getInventoriesForOrderItems(orderItems);
        orderItems.forEach(item -> {
            final Inventory inventory = inventoriesByIds.get(item.getInventoryId());
            if (inventory == null) {
                throw new NotFoundException("Inventory not found");
            }

            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            em.persist(inventory);
        });
    }

    public void deleteInventory(final Integer id) {
        log.info("Deleting inventory: " + id);
        final Inventory inventory = inventoryQueryService.getByIdForUpdate(id);
        orderQueryService.deleteOrderItemsForInventory(id);
        em.remove(inventory);
    }

    public InventoryResult getInventory(final Integer id) {
        return inventoryQueryService.findById(id)
                .map(inventory -> new InventoryResult(inventory.getQuantity(), inventory.getPrice()))
                .orElse(new InventoryResult(0L, 0L));
    }

    public List<InventoryResult> getInventories(final List<Integer> ids) {
        return inventoryQueryService.getAll(ids, false).stream()
                .map(inventory -> new InventoryResult(inventory.getQuantity(), inventory.getPrice()))
                .toList();
    }

    public Map<Integer, Long> getCostsByInventory(final Set<Integer> inventoryIds) {
        final List<Inventory> inventories = inventoryQueryService.getAll(inventoryIds, false);
        return inventories.stream()
                .collect(Collectors.toMap(Inventory::getId, Inventory::getPrice));
    }

    private Map<Integer, Inventory> getInventoriesForOrderItems(final List<OrderItem> orderItems) {
        final List<Integer> inventoryIds = orderItems.stream()
                .map(OrderItem::getInventoryId)
                .toList();
        return inventoryQueryService.getAll(inventoryIds, true).stream()
                .collect(Collectors.toMap(Inventory::getId, Function.identity()));
    }

}
