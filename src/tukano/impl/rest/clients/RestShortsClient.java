package tukano.impl.rest.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.api.rest.RestUsers;

import java.net.URI;
import java.util.List;


public class RestShortsClient extends RestClient implements Shorts {

    final WebTarget target;

    public RestShortsClient( URI serverURI ) {
        super(serverURI);
        target = client.target( serverURI ).path( RestShorts.PATH );
    }


    @Override
    public Result<Short> createShort(String userId, String password) {
        return null;
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        return super.reTry(() -> super.toJavaResultVoid(
                        target
                        .path(shortId)
                        .queryParam(RestShorts.PWD, password)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .delete()));
    }

    @Override
    public Result<Short> getShort(String shortId) {
        return super.reTry(() -> super.toJavaResult(
                        target
                        .path(shortId)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get(),Short.class));
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        return super.reTry(() -> super.toJavaResultList(
                        target
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
                        target
                        .path(shortId + "/" + userId + RestShorts.LIKES)
                        .queryParam(RestShorts.PWD, password)
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(isLiked, MediaType.APPLICATION_JSON))));
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        return super.reTry(() -> super.toJavaResultList(
                        target
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
