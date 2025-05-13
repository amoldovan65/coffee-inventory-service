package moldo.coffee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(OrderItemId.class)
@NamedNativeQueries({
        @NamedNativeQuery(name = OrderItem.COUNT_BY_INVENTORY_ID_AND_ORDER_STATUS,
                query = "SELECT COUNT(*) FROM order_item oi " +
                        "INNER JOIN bean_order bo ON bo.id = oi.order_id " +
                        "WHERE oi.bean_id = :inventoryId AND bo.status = :status",
                resultClass = Long.class)
})
@NamedQueries({
        @NamedQuery(name = OrderItem.DELETE_BY_INVENTORY_ID, query = "delete from OrderItem where inventoryId = :inventoryId"),
        @NamedQuery(name = OrderItem.DELETE_BY_ORDER_IDS, query = "delete from OrderItem where orderId in :orderIds"),
        @NamedQuery(name = OrderItem.GET_ALL_BY_ORDER_IDS, query = "select oi from OrderItem oi where oi.orderId in :orderIds")
})
@Table(name = "order_item")
public class OrderItem {

    public static final String COUNT_BY_INVENTORY_ID_AND_ORDER_STATUS = "OrderItem.countByInventoryIdAndOrderStatus";
    public static final String DELETE_BY_INVENTORY_ID = "OrderItem.deleteByInventoryId";
    public static final String DELETE_BY_ORDER_IDS = "OrderItem.deleteByOrderIds";
    public static final String GET_ALL_BY_ORDER_IDS = "OrderItem.getAllByOrderIds";

    @Id
    @Column(name = "bean_id")
    private Integer inventoryId;

    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Column(nullable = false, columnDefinition = "numeric")
    private Long quantity;

}
