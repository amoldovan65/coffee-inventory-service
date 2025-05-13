package moldo.coffee.web.domain;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {

    @NotNull
    private Integer beanId;

    @NotNull
    private Long quantity;

}
