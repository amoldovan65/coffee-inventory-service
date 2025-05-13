package moldo.coffee.web.domain;

import moldo.coffee.domain.OrderStatus;

import java.util.List;

public record OrderResult(OrderStatus status, Long cost, List<ItemResult> items) {
}
