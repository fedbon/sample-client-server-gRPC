package ru.fedbon;

import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fedbon.service.RemoteNumServiceImpl;
import java.io.IOException;


public class GRPCServer {
    private static final Logger log = LoggerFactory.getLogger(GRPCServer.class);
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        var numbersService = new RemoteNumServiceImpl();

        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(numbersService).build();
        server.start();
        log.info("server waiting for client connections...");
        server.awaitTermination();
    }
}
