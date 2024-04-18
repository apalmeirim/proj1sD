package tukano.impl.rest.clients;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import tukano.api.Likes;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import utils.Sleep;

import java.net.URI;
import java.util.List;


public class RestShortsClient extends RestClient implements Shorts {

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
        return super.reTry(() -> super.toJavaResultVoid(
                client.target(serverURI).path(RestShorts.PATH)
                        .path(shortId)
                        .queryParam(password)
                        .request()
                        .delete()));
    }

    @Override
    public Result<Short> getShort(String shortId) {
        return super.reTry(() -> super.toJavaResult(
                client.target(serverURI).path(RestShorts.PATH)
                        .path(shortId)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get(),Short.class));
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        return super.reTry(() -> super.toJavaResultList(
                client.target(serverURI).path(RestShorts.PATH)
                        .path( userId + RestShorts.SHORTS )
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get()));
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
        return super.reTry(() -> super.toJavaResultVoid(
                client.target(serverURI).path(RestShorts.PATH)
                        .path(shortId + "/" + userId + RestShorts.LIKES)
                        .queryParam(RestShorts.PWD, password)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(isLiked, MediaType.APPLICATION_JSON))));
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        return super.reTry(() -> super.toJavaResultList(
                client.target(serverURI).path(RestShorts.PATH)
                        .path(shortId + RestShorts.LIKES)
                        .queryParam(RestShorts.PWD, password)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get()));
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        return null;
    }



}
