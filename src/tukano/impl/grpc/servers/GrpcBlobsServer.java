package tukano.impl.grpc.servers;

import java.net.InetAddress;
import java.util.logging.Logger;

import io.grpc.ServerBuilder;
import tukano.api.java.Users;
import tukano.impl.java.Discovery;

public class GrpcBlobsServer {
    public static final int PORT = 15678;

    private static final String GRPC_CTX = "/grpc";
    private static final String SERVER_BASE_URI = "grpc://%s:%s%s";

    private static Logger Log = Logger.getLogger(GrpcBlobsServer.class.getName());

    public static void main(String[] args) throws Exception {

        var stub = new GrpcBlobsServerStub();
        var server = ServerBuilder.forPort(PORT).addService(stub).build();
        var serverURI = String.format(SERVER_BASE_URI, InetAddress.getLocalHost().getHostAddress(), PORT, GRPC_CTX);

        Discovery.getInstance().announce("blobs", serverURI);
        Log.info(String.format("%s gRPC Server ready @ %s\n", Users.NAME, serverURI));
        server.start().awaitTermination();
    }
}
