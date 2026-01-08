package org.example.stocktradingclient.controller;

import org.example.stocktradingclient.dto.StockResponseDTO;
import org.example.stocktradingclient.service.StockClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @GetMapping(value = "/subscribe/stock/price",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeStockPrice() {
        return stockClientService.subscribeStockPrice();
    }
}
