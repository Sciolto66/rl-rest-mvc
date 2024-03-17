package nl.rowendu.rlrestmvc.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Customer {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;
    private String name;
    @Column(length = 255)
    private String email;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Builder.Default
    @OneToMany(mappedBy = "customer")
    private Set<BeerOrder> beerOrders = new HashSet<>();
}
