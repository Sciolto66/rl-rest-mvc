package nl.rowendu.rlrestmvc.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Beer {

    @Id
    @UuidGenerator
    @Column(
            length = 36,
            columnDefinition = "varchar",
            updatable = false,
            nullable = false
    )
    private UUID id;

    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Size(max = 50, message = "Beer name is too long")
    @Column(length = 50)
    private String beerName;

    @NotNull
    private BeerStyle beerStyle;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String upc;

    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
