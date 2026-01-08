package org.example.stocktradingclient.service;

import com.example.grpc.*;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.stocktradingclient.dto.OrderSummaryResponseDTO;
import org.example.stocktradingclient.dto.StockOrderRequestDTO;
import org.example.stocktradingclient.dto.StockResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
public class StockClientService {

    //blocking stub is correct fit for unary
    //because we're relying on request / response model
    //note that blocking stub you cannot use for streaming client or bidirectional streaming
    @GrpcClient("stockBlockStubService")
    private StockTradingServiceInterfaceGrpc.StockTradingServiceInterfaceBlockingStub stockBlockingStub;

    //non-blocking stub
    @GrpcClient("stockStubService")
    private StockTradingServiceInterfaceGrpc.StockTradingServiceInterfaceStub stockAsyncStub;

    public StockResponseDTO getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        StockResponse response = stockBlockingStub.getStockPrice(request);
        return new StockResponseDTO(response.getStockSymbol(), response.getPrice(), response.getTimestamp());
    }

    public SseEmitter subscribeStockPrice() {
        SseEmitter emitter = new SseEmitter();

        Empty emptyRequest = Empty.newBuilder().build();
        stockAsyncStub.subscribeStockPrice(emptyRequest, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                try {
                    StockResponseDTO dto = new StockResponseDTO(
                            stockResponse.getStockSymbol(),
                            stockResponse.getPrice(),
                            stockResponse.getTimestamp()
                    );
                    emitter.send(dto);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                emitter.completeWithError(throwable);
            }

            @Override
            public void onCompleted() {
                emitter.complete();
            }
        });

        return emitter;
    }

    public SseEmitter bulkStockOrder(List<StockOrderRequestDTO> orders) {

        SseEmitter emitter = new SseEmitter();


        StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                try {
                    OrderSummaryResponseDTO responseDTO = new OrderSummaryResponseDTO(
                            orderSummary.getTotalOrders(),
                            orderSummary.getTotalAmount(),
                            orderSummary.getSuccessCount()
                    );
                    emitter.send(responseDTO);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error receiving order summary: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Order summary stream completed.");
                emitter.complete();
            }
        };
        StreamObserver<StockOrder> requestObserver = stockAsyncStub.bulkStockOrder(responseObserver);

        // send multiple stream of stock orders as message/request

        orders.forEach(
                order -> {
                    try {
                        StockOrder stockOrder = StockOrder.newBuilder()
                                .setStockSymbol(order.stock_symbol())
                                .setOrderType(order.order_type())
                                .setPrice(order.price())
                                .setQuantity(order.quantity())
                                .build();
                        requestObserver.onNext(stockOrder);
                    } catch (Exception e) {
                        responseObserver.onError(e);
                    }
                }
        );
        //done sending order
        requestObserver.onCompleted();

        return emitter;
    }

}
