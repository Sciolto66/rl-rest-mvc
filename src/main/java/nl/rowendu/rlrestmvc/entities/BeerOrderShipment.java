package nl.rowendu.rlrestmvc.entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class BeerOrderShipment {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
    private UUID id;

    @Version
    private Long version;

    @OneToOne
    private BeerOrder beerOrder;

    private String trackingNumber;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeerOrderShipment that)) return false;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(version, that.version)) return false;
        if (!Objects.equals(beerOrder, that.beerOrder)) return false;
        if (!Objects.equals(trackingNumber, that.trackingNumber))
            return false;
        if (!Objects.equals(createdDate, that.createdDate)) return false;
        return Objects.equals(lastModifiedDate, that.lastModifiedDate);
    }

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
