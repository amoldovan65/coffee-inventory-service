package moldo.coffee.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OrderItemId implements Serializable {

    private Integer inventoryId;
    private Integer orderId;

}
