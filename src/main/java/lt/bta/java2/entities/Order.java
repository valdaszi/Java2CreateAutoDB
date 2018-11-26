package lt.bta.java2.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "order")
@NamedEntityGraph(name = Order.GRAPH_ITEMS,
        attributeNodes = @NamedAttributeNode(value = "items", subgraph = "items"),
        subgraphs = @NamedSubgraph(name = "items", attributeNodes = @NamedAttributeNode("part")))
public class Order {

    public static final String GRAPH_ITEMS = "graph.Order.items";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDate date;

    @Column
    private String no;

    @Column
    private BigDecimal total;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();


    // specialus laukai - nemodifikuojami is programos tiesiogiai

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


    // metodai skirti prideti/istrinti order item

    public void addItem(OrderItem item) {
        item.setOrder(this);
        if (getItems() == null) setItems(new ArrayList<>());
        getItems().add(item);

        item.setTotal(item.getPrice().multiply(item.getQuantity()));
        calcTotals();
    }

    public void removeItem(OrderItem item) {
        if (getItems() == null) return;
        getItems().remove(item);
        item.setPart(null);
        calcTotals();
    }

    private void calcTotals() {
        if (getItems() == null) {
            setTotal(null);
            return;
        }
        setTotal(
                getItems()
                        .stream()
                        .map(OrderItem::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    // Standartiniai geteriai ir seteriai - galima pergeneruoti

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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public int getVersion() {
        return version;
    }
}
