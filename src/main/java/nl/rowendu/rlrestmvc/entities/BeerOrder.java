package nl.rowendu.rlrestmvc.entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
public class BeerOrder {

    public BeerOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef,
                     Customer customer, Set<BeerOrderLine> beerOrderLines, BeerOrderShipment beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customerRef = customerRef;
        this.setCustomer(customer);
        this.beerOrderLines = beerOrderLines;
        this.beerOrderShipment = beerOrderShipment;
    }

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    public boolean isNew() {
        return this.id == null;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            customer.getBeerOrders().add(this);
        }
    }

    @OneToMany(mappedBy = "beerOrder")
    private Set<BeerOrderLine> beerOrderLines;

    @OneToOne
    private BeerOrderShipment beerOrderShipment;
}
