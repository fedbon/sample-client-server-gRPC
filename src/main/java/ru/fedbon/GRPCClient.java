package ru.fedbon;

import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fedbon.generated.NumberRequest;
import ru.fedbon.generated.RemoteNumServiceGrpc;
import ru.fedbon.observer.ClientStreamObserver;

import java.util.concurrent.CountDownLatch;


public class GRPCClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final Logger log = LoggerFactory.getLogger(GRPCClient.class);
    private static final int LOOP_SIZE = 50;

    private long value = 0;

    public static void main(String[] args) throws InterruptedException {
        log.info("client started...");

        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var latch = new CountDownLatch(1);
        var stub = RemoteNumServiceGrpc.newStub(channel);
        new GRPCClient().clientAction(stub);
        log.info("client finished...");
        latch.await();
        channel.shutdown();
    }

    private void clientAction(RemoteNumServiceGrpc.RemoteNumServiceStub stub) {
        var requestSequence = NumberRequest.newBuilder().setFirstValue(0).setLastValue(30).build();
        var clientStreamObserver = new ClientStreamObserver();
        stub.getStreamFromServer(requestSequence, clientStreamObserver);
        for (int i = 0; i < LOOP_SIZE; i++) {
            var valForPrint = getNextValue(clientStreamObserver);
            log.info("current value:{}", valForPrint);
            sleep();
        }
    }

    private long getNextValue(ClientStreamObserver clientStreamObserver) {
        value = value + clientStreamObserver.getLastValueAndReset() + 1;
        return value;
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
