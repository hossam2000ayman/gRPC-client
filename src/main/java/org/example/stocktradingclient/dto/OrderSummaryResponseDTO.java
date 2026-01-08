package org.example.stocktradingclient.dto;

public record OrderSummaryResponseDTO(
        Integer totalOrders, Double totalAmount, Integer successCount
) {
    public OrderSummaryResponseDTO() {
        this(null, null, null);
    }
    
}
