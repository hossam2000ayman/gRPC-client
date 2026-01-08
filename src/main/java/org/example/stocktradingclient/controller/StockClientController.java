package org.example.stocktradingclient.controller;

import org.example.stocktradingclient.dto.StockOrderRequestDTO;
import org.example.stocktradingclient.dto.StockResponseDTO;
import org.example.stocktradingclient.service.StockClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockClientController {

    private final StockClientService stockClientService;

    public StockClientController(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

    @GetMapping("/get/stock/price")
    public StockResponseDTO getStockPrice(@RequestParam(name = "symbol") String stockSymbol) {
        return stockClientService.getStockPrice(stockSymbol);
    }

    @GetMapping(value = "/subscribe/stock/price", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeStockPrice() {
        return stockClientService.subscribeStockPrice();
    }

    @PostMapping(value = "/place/bulk/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter placeBulkOrders(@RequestBody List<StockOrderRequestDTO> orders) {
        return stockClientService.bulkStockOrder(orders);
    }
}
