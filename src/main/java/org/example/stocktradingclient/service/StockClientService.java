package org.example.stocktradingclient.service;

import com.example.grpc.StockRequest;
import com.example.grpc.StockResponse;
import com.example.grpc.StockTradingServiceInterfaceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.stocktradingclient.dto.StockResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    //blocking stub is correct fit for unary
    //because we're relying on request / response model
    //note that blocking stub you cannot use for streaming client or bidirectional streaming
    @GrpcClient("stockStubService")
    private StockTradingServiceInterfaceGrpc.StockTradingServiceInterfaceBlockingStub stockBlockingStub;

    public StockResponseDTO getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        StockResponse response = stockBlockingStub.getStockPrice(request);
        return new StockResponseDTO(response.getStockSymbol(), response.getPrice(), response.getTimestamp());
    }

}
