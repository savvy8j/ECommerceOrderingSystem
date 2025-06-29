package org.example.db;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantityOrdered;

    private Double priceAtOrderTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;


    @Column(name="product_id")
    private Long productId;


}