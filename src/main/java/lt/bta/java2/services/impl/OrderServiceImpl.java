package lt.bta.java2.services.impl;

import lt.bta.java2.entities.Order;

import lt.bta.java2.jpa.PersistenceExecutor;
import lt.bta.java2.services.i.OrderService;
import lt.bta.java2.services.results.PartReport;

import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderServiceImpl implements OrderService {

    @Override
    @POST
    @Path("/")
    public Response create(Order order) {
        Order newOrder = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            order.setId(null);
            entityManager.persist(order);
            return order;
        });
        return Response.status(Response.Status.CREATED).entity(newOrder).build();
    }

    @Override
    @GET
    @Path("/{orderId}")
    public Response read(@PathParam("orderId") int orderId) {
        Order order = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Map<String, Object> hints = new HashMap<>();
            EntityGraph graph = entityManager.getEntityGraph(Order.GRAPH_ITEMS);
            hints.put("javax.persistence.fetchgraph", graph);

            Order entity = entityManager.find(Order.class, orderId, hints);
            if (entity == null) throw new NotFoundException();

            return entity;
        });

        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @Override
    @PUT
    @Path("/{orderId}")
    public Response update(@PathParam("orderId") int orderId, Order order) {
        Order updatedOrder = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Order entity = entityManager.find(Order.class, orderId);
            if (entity == null) throw new NotFoundException();

            entity.setDate(order.getDate());
            entity.setNo(order.getNo());

            entityManager.persist(entity);
            return entity;
        });
        return Response.status(Response.Status.OK).entity(updatedOrder).build();
    }

    @Override
    @DELETE
    @Path("/{orderId}")
    public Response delete(@PathParam("orderId") int orderId) {
        PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Order entity = entityManager.find(Order.class, orderId);
            if (entity == null) throw new NotFoundException();
            entityManager.remove(entity);
            return null;
        });
        return Response.status(Response.Status.OK).build();
    }


    @Override
    @GET
    @Path("/totalPartsOrdered")
    public Response totalPartsOrdered(@QueryParam("from") String from, @QueryParam("to") String to) {
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);
        List<PartReport> parts = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            TypedQuery<PartReport> query = entityManager.createQuery(
                    "select new lt.bta.java2.services.results.PartReport" +
                            "(i.part.id, i.part.sku, i.part.name, sum(i.quantity), sum(i.total))" +
                            " from OrderItem i" +
                            " where i.order.date >= :dateFrom and i.order.date < :dateTo" +
                            " group by i.part",
                    PartReport.class);
            query.setParameter("dateFrom", dateFrom);
            query.setParameter("dateTo", dateTo);
            return query.getResultList();
        });
        return Response.status(Response.Status.OK).entity(parts).build();
    }

    @GET
    @Path("/test-query")
    public Response testQuery(@QueryParam("from") String from, @QueryParam("to") String to) {
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);
        List parts = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Query query = entityManager.createQuery(
                    "select i.part, sum(i.quantity), sum(i.total)" +
                            " from OrderItem i" +
                            " where i.order.date >= :dateFrom and i.order.date < :dateTo" +
                            " group by i.part"
                    );
            query.setParameter("dateFrom", dateFrom);
            query.setParameter("dateTo", dateTo);
            return query.getResultList();
        });
        return Response.status(Response.Status.OK).entity(parts).build();
    }
}
