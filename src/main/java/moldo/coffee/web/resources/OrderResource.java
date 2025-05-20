package moldo.coffee.web.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;
import moldo.coffee.domain.OrderStatus;
import moldo.coffee.service.OrderService;
import moldo.coffee.web.domain.ItemRequest;
import moldo.coffee.web.domain.OrderResult;

import java.util.List;

@Path("/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    @POST
    public void placeOrder(@QueryParam("clientId") @NotNull final Integer clientId, @NotEmpty final List<@Valid ItemRequest> items) {
        orderService.createOrder(clientId, items);
    }

    @PUT
    @Path("/{id}/{status}")
    public void updateStatus(@PathParam("id") final Integer id, @PathParam("status") final OrderStatus status) {
        orderService.updateOrderStatus(id, status);
    }

    @DELETE
    public void deleteOrdersOfClient(@QueryParam("clientId") @NotNull final Integer clientId) {
        orderService.deleteOrdersByClientId(clientId);
    }

    @DELETE
    @Path("/{id}")
    public void deleteOrder(@PathParam("id") final Integer id) {
        orderService.deleteOrder(id);
    }

    @GET
    public List<OrderResult> getOrdersOfClient(@QueryParam("clientId") final Integer clientId,
                                               @QueryParam("status") final OrderStatus status) {
        return orderService.getOrdersOfClient(clientId, status);
    }

}
