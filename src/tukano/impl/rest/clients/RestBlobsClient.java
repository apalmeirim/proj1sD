package tukano.impl.rest.clients;
import jakarta.ws.rs.client.WebTarget;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.rest.RestBlobs;
import java.net.URI;


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