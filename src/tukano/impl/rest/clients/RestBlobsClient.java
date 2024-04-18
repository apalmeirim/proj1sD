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

public class RestBlobsClient implements Blobs {

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 1000;
    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestBlobsClient(URI serverURI) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();
        this.client = ClientBuilder.newClient(config);

        target = client.target(serverURI).path(RestBlobs.PATH);
    }

    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        return null;
    }

    @Override
    public Result<byte[]> download(String blobId) { return null; }


}