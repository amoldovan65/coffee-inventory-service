package moldo.coffee.web.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryCreateRequest {

    @NotNull
    private Integer beanId;

    @NotNull
    private Long quantity;

    @NotNull
    private Long price;

}
