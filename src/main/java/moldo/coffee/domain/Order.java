package moldo.coffee.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bean_order")
@NamedQueries({
        @NamedQuery(name = Order.GET_ALL_BY_CLIENT_ID, query = "select o from Order o where o.clientId = :clientId"),
        @NamedQuery(name = Order.GET_ALL_BY_CLIENT_ID_AND_STATUS, query = "select o from Order o where o.clientId = :clientId and o.status = :status")
})
public class Order {

    public static final String GET_ALL_BY_CLIENT_ID = "Order.getAllByClientId";
    public static final String GET_ALL_BY_CLIENT_ID_AND_STATUS = "Order.getAllByClientIdAndStatus";

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

}
