package moldo.coffee.web.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryEditRequest {

    @NotNull
    private Long quantity;

    @NotNull
    private Long price;

}
