package lt.bta.java2.services.i;

import lt.bta.java2.entities.Invoice;
import lt.bta.java2.entities.InvoiceItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/invoice")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface InvoiceService {

    @POST
    @Path("/")
    Response create(Invoice invoice);

    @GET
    @Path("/{id}")
    Response read(@PathParam("id") int id);

    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") int id, Invoice invoice);

    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") int id);



    @POST
    @Path("/{id}")
    Response createItem(@PathParam("id") int id, InvoiceItem item);

    @PUT
    @Path("/{id}/{itemId}")
    Response updateItem(@PathParam("id") int id, @PathParam("itemId") int itemId, InvoiceItem item);

    @DELETE
    @Path("/{id}/{itemId}")
    Response deleteItem(@PathParam("id") int id, @PathParam("itemId") int itemId);

    @GET
    @Path("/total-parts")
    Response totalParts(@QueryParam("from") String from, @QueryParam("to") String to);

    @GET
    @Path("/totals")
    Response totals(@QueryParam("from") String from, @QueryParam("to") String to);

}
