package org.example.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.db.OrderItem;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequestDTO {
    private Long userId;
    private List<OrderItemDTO> orderItems;
}
