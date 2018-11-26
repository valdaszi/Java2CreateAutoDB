package lt.bta.java2.services.i;

import lt.bta.java2.entities.Order;

import javax.ws.rs.core.Response;

public interface OrderService {

    Response create(Order order);

    Response read(int orderId);

    Response update(int orderId, Order order);

    Response delete(int orderId);


    Response totalPartsOrdered(String from, String to);

}
