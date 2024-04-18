package tukano.impl.rest.clients;

import io.grpc.xds.shaded.io.envoyproxy.envoy.config.overload.v3.ScaledTrigger;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.api.rest.RestUsers;
import utils.Sleep;

import java.net.URI;
import java.util.List;

import static tukano.impl.rest.clients.RestClient.getErrorCodeFrom;

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
        WebTarget target = client.target(serverURI).path(RestShorts.PATH);
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                Response r = target.path( shortId )
                        .queryParam(RestShorts.PWD, password).request()
                        .delete();

                if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
                    // SUCCESS
                    return Result.ok();
                else {
                    return Result.error(getErrorCodeFrom(r.getStatus()));
                }
            } catch (ProcessingException x) {
                Sleep.ms(RETRY_SLEEP);
            }
        return null; // Report failure
    }

    @Override
    public Result<Short> getShort(String shortId) {
        return null;
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        WebTarget target = client.target(serverURI).path(RestShorts.PATH);
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                Response r = target.path( userId + RestShorts.SHORTS )
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get();

                if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
                    // SUCCESS
                    return Result.ok(r.readEntity(new GenericType<List<String>>() {}));
                else {
                    return Result.error(getErrorCodeFrom(r.getStatus()));
                }
            } catch (ProcessingException x) {
                Sleep.ms(RETRY_SLEEP);
            }
        return null; // Report failure
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

}
