package moldo.coffee.web.resources;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;
import moldo.coffee.service.InventoryService;
import moldo.coffee.web.domain.InventoryCreateRequest;
import moldo.coffee.web.domain.InventoryEditRequest;
import moldo.coffee.web.domain.InventoryResult;

@Path("")
@RequiredArgsConstructor
public class InventoryResource {

    private final InventoryService inventoryService;

    @POST
    public void createInventory(@Valid final InventoryCreateRequest request) {
        inventoryService.createInventory(request);
    }

    @PUT
    @Path("/{id}")
    public void updateInventory(@PathParam("id") final Integer id, @Valid final InventoryEditRequest request) {
        inventoryService.updateInventory(id, request);
    }

    @DELETE
    @Path("/{id}")
    public void deleteInventory(@PathParam("id") final Integer id) {
        inventoryService.deleteInventory(id);
    }

    @GET
    @Path("/{id}")
    public InventoryResult getInventoryForBeans(@PathParam("id") final Integer id) {
        return inventoryService.getInventory(id);
    }

}
