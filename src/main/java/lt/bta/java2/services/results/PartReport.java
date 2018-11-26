package lt.bta.java2.services.results;

import lt.bta.java2.entities.Part;

import java.math.BigDecimal;

public class PartReport {

    private Part part;

    private BigDecimal quantity;

    private BigDecimal total;

    public PartReport(Integer id, String sku, String name,
                      BigDecimal quantity, BigDecimal total) {
        this.part = new Part();
        this.part.setId(id);
        this.part.setSku(sku);
        this.part.setName(name);

        this.part.setPrice(total.divide(quantity, BigDecimal.ROUND_HALF_UP));

        this.quantity = quantity;
        this.total = total;
    }

    // standartiniai geteriai:

    public Part getPart() {
        return part;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
