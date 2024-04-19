package tukano.impl.rest.clients;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestBlobs;
import tukano.api.rest.RestShorts;
import utils.Sleep;

import java.net.URI;

import static tukano.impl.rest.clients.RestClient.getErrorCodeFrom;

public class RestBlobsClient extends RestClient implements Blobs {

    final WebTarget target;

    public RestBlobsClient(URI serverURI) {
        super(serverURI);
        target = client.target(serverURI).path(RestBlobs.PATH);
    }

    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        return null;
    }

    @Override
    public Result<byte[]> download(String blobId) { return null; }


}