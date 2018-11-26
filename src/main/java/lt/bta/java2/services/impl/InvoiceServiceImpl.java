package lt.bta.java2.services.impl;

import lt.bta.java2.entities.Invoice;
import lt.bta.java2.entities.InvoiceItem;
import lt.bta.java2.entities.Order;
import lt.bta.java2.jpa.PersistenceExecutor;
import lt.bta.java2.services.i.InvoiceService;
import lt.bta.java2.services.results.PartReport;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceServiceImpl implements InvoiceService {

    @Override
    public Response create(Invoice invoice) {
        Invoice newInvoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            entityManager.persist(invoice);
            return invoice;
        });
        return Response.status(Response.Status.OK).entity(newInvoice).build();
    }

    @Override
    public Response read(int id) {
        Invoice invoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Map<String, Object> hints = new HashMap<>();
            EntityGraph graph = entityManager.getEntityGraph(Invoice.GRAPH_ITEMS);
            hints.put("javax.persistence.fetchgraph", graph);

            return entityManager.find(Invoice.class, id, hints);
        });
        return Response.status(Response.Status.OK).entity(invoice).build();
    }

    @Override
    public Response update(int id, Invoice invoice) {
        Invoice updatedInvoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
//            Invoice oldInvoice = entityManager.find(Invoice.class, id);
//            if (oldInvoice == null) throw new NotFoundException("Invoice not found with id " + id);
//
//            oldInvoice.setDate(invoice.getDate());
//            oldInvoice.setSerial(invoice.getSerial());
//            oldInvoice.setNo(invoice.getNo());
//
//            entityManager.persist(oldInvoice);
//            return oldInvoice;

            invoice.setId(id);
            entityManager.merge(invoice);
            return invoice;

        });
        return Response.status(Response.Status.OK).entity(updatedInvoice).build();
    }

    @Override
    public Response delete(int id) {
        PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Invoice oldInvoice = entityManager.find(Invoice.class, id);
            if (oldInvoice == null) throw new NotFoundException("Invoice not found with id " + id);
            entityManager.remove(oldInvoice);
            return null;
        });
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response createItem(int id, InvoiceItem item) {
        Invoice updatedInvoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Invoice oldInvoice = entityManager.find(Invoice.class, id);
            if (oldInvoice == null) throw new NotFoundException("Invoice not found with id " + id);

            oldInvoice.addItem(item);

            entityManager.persist(oldInvoice);
            return oldInvoice;
        });
        return Response.status(Response.Status.OK).entity(updatedInvoice).build();
    }

    @Override
    public Response updateItem(int id, int itemId, InvoiceItem item) {
        Invoice updatedInvoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Invoice oldInvoice = entityManager.find(Invoice.class, id);
            if (oldInvoice == null) throw new NotFoundException("Invoice not found with id " + id);

            oldInvoice.updateItem(itemId, item);

            entityManager.persist(oldInvoice);
            return oldInvoice;
        });
        return Response.status(Response.Status.OK).entity(updatedInvoice).build();
    }

    @Override
    public Response deleteItem(int id, int itemId) {
        Invoice updatedInvoice = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            Invoice oldInvoice = entityManager.find(Invoice.class, id);
            if (oldInvoice == null) throw new NotFoundException("Invoice not found with id " + id);

            oldInvoice.removeItem(itemId);

            entityManager.persist(oldInvoice);
            return oldInvoice;
        });
        return Response.status(Response.Status.OK).entity(updatedInvoice).build();
    }

    @Override
    public Response totalParts(String from, String to) {
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);
        List<PartReport> parts = PersistenceExecutor.executeInsideTransaction(entityManager -> {
            TypedQuery<PartReport> query = entityManager.createQuery(
                    "select new lt.bta.java2.services.results.PartReport(i.part.id, i.part.sku, i.part.name, sum(i.quantity), sum(i.total))" +
                            " from InvoiceItem i" +
                            " where i.invoice.date >= :dateFrom and i.invoice.date < :dateTo" +
                            " group by i.part"
            , PartReport.class);
            query.setParameter("dateFrom", dateFrom);
            query.setParameter("dateTo", dateTo);
            return query.getResultList();
        });
        return Response.status(Response.Status.OK).entity(parts).build();
    }

    @Override
    public Response totals(String from, String to) {
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);

        Map<String, Object> totals = new HashMap<>();
        EntityManager em = PersistenceExecutor.createEntityManager();
        try {

            Query query = em.createQuery(
                    "select sum(i.total) from Invoice i" +
                    " where i.date >= :dateFrom and i.date < :dateTo");
            query.setParameter("dateFrom", dateFrom);
            query.setParameter("dateTo", dateTo);

            totals.put("totals", query.getSingleResult());


        } finally {
            em.close();
        }
        return Response.status(Response.Status.OK).entity(totals).build();
    }
}
