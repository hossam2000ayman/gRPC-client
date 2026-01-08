package org.example.stocktradingclient.dto;


public record StockOrderRequestDTO(
        String stock_symbol, String order_type, Double price, Integer quantity
) {
}
