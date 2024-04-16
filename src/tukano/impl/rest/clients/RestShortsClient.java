package tukano.impl.rest.clients;

import io.grpc.xds.shaded.io.envoyproxy.envoy.config.overload.v3.ScaledTrigger;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;

import java.net.URI;
import java.util.List;

public class RestShortsClient implements Shorts {

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 1000;
    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    public RestShortsClient( URI serverURI ) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();
        this.client = ClientBuilder.newClient(config);

        target = client.target( serverURI ).path( RestShorts.PATH );
    }


    @Override
    public Result<Short> createShort(String userId, String password) {
        return null;
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        return null;
    }

    @Override
    public Result<Short> getShort(String shortId) {
        return null;
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        return null;
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        return null;
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        return null;
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        return null;
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        return null;
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        return null;
    }

    @Override
    public Result<String> hasBlobId(String blobId) {
        return null;
    }

}
