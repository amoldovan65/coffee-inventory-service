package moldo.coffee.web.domain;

import moldo.coffee.domain.OrderStatus;

import java.util.List;
import java.util.UUID;

public record OrderResult(OrderStatus status, UUID orderNumber, Long cost, List<ItemResult> items) {
}
