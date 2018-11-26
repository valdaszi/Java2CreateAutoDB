package lt.bta.java2.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@NamedEntityGraph(name = Invoice.GRAPH_ITEMS,
        attributeNodes = @NamedAttributeNode(value = "items", subgraph = "items"),
        subgraphs = @NamedSubgraph(name = "items", attributeNodes = @NamedAttributeNode("part")))
public class Invoice {

    public static final String GRAPH_ITEMS = "graph.Invoice.items";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate date;

    private String serial;

    private int no;

    private BigDecimal total;

    @OneToMany(mappedBy = "invoice",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties("invoice")
    private List<InvoiceItem> items;

    @Version
    private int version = 1;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime updated;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }


    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        if (getItems() == null) setItems(new ArrayList<>());
        getItems().add(item);

        item.setTotal(item.getPrice().multiply(item.getQuantity()));
        calcTotals();
    }

    public void removeItem(int itemId) {
        if (getItems() != null) {
            getItems().removeIf(x -> x.getId() == itemId);
        }
        calcTotals();
    }

    public void updateItem(int itemId, InvoiceItem item) {
        if (getItems() != null) {
            item.setTotal(item.getPrice().multiply(item.getQuantity()));
            getItems()
                    .stream()
                    .filter(x -> Objects.equals(x.getId(), itemId))
                    .findAny()
                    .ifPresent(x -> {
                        x.setPart(item.getPart());
                        x.setPrice(item.getPrice());
                        x.setQuantity(item.getQuantity());
                        x.setTotal(item.getTotal());
                    });
        }
        calcTotals();
    }

    private void calcTotals() {
        if (getItems() == null) {
            setTotal(null);
            return;
        }
        setTotal(getItems()
                .stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    // getters & setters:


    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public int getVersion() {
        return version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }
}
