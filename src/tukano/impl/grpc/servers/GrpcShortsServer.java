package tukano.impl.grpc.servers;

import java.net.InetAddress;
import java.util.logging.Logger;

import io.grpc.ServerBuilder;
import tukano.api.java.Shorts;
import tukano.impl.java.Discovery;

public class GrpcShortsServer {
    public static final int PORT = 14567;

    private static final String GRPC_CTX = "/grpc";
    private static final String SERVER_BASE_URI = "grpc://%s:%s%s";

    private static Logger Log = Logger.getLogger(GrpcShortsServer.class.getName());

    public static void main(String[] args) throws Exception {

        var stub = new GrpcShortsServerStub();
        var server = ServerBuilder.forPort(PORT).addService(stub).build();
        var serverURI = String.format(SERVER_BASE_URI, InetAddress.getLocalHost().getHostAddress(), PORT, GRPC_CTX);

        Discovery.getInstance().announce("shorts", serverURI);
        Log.info(String.format("%s gRPC Server ready @ %s\n", Shorts.NAME, serverURI));
        server.start().awaitTermination();
    }
}
