package moldo.coffee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@NamedQueries({
        @NamedQuery(name = Inventory.FIND_ALL_BY_ID, query = "select i from Inventory i where i.id in :ids")
})
@Table(name = "inventory")
public class Inventory {

    public static final String FIND_ALL_BY_ID = "Inventory.findAllById";

    @Id
    @Column(name = "bean_id")
    private Integer id;

    @Column(nullable = false, columnDefinition = "numeric")
    private Long quantity;

    @Column(nullable = false, columnDefinition = "numeric")
    private Long price;

}
