package tukano.impl.java;

import tukano.api.java.Blobs;
import tukano.impl.grpc.clients.GrpcBlobsClient;

public class BlobsClientFactory {

    public static Blobs getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("BlobsService", 1)[0];
        if (serverURI.getFragment().contains("rest")) return new RestBlobsClient(serverURI);
        else return new GrpcBlobsClient(serverURI);
    }


}

