package tukano.impl;

import tukano.api.java.Blobs;
import tukano.impl.grpc.clients.GrpcBlobsClient;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.java.Discovery;
import tukano.impl.rest.clients.RestBlobsClient;

import java.net.URI;
import java.util.List;

public class BlobsClientFactory {

    static int currentIndex = 0;


    public static Blobs getClients() {
        var serverURI = getServerURI();
        if (serverURI.toString().contains("rest")) return new RestBlobsClient(serverURI);
        else return new GrpcBlobsClient(serverURI);
    }



    public static URI getServerURI() {
        try {
            URI[] allURIs = Discovery.getInstance().knownUrisOf("blobs", 1);
            URI uri = allURIs[currentIndex];
            currentIndex = (currentIndex + 1) % allURIs.length;
            return uri;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}

