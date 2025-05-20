package moldo.coffee.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bean_order")
@NamedQueries({
        @NamedQuery(name = Order.GET_ALL_BY_CLIENT_ID, query = "select o from Order o where o.clientId = :clientId")
})
public class Order {

    public static final String GET_ALL_BY_CLIENT_ID = "Order.getAllByClientId";

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "order_number", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID orderNumber;

}
