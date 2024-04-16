package tukano.impl.rest.servers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.impl.java.JavaShorts;

import java.util.List;

public class RestShortsResource implements RestShorts {

    final Shorts impl;
    public RestShortsResource() {
        this.impl = new JavaShorts();
    }

    @Override
    public Short createShort(String userId, String password) {
        return resultOrThrow( impl.createShort(userId, password));
    }

    @Override
    public void deleteShort(String shortId, String password) {
        resultOrThrow(impl.deleteShort(shortId, password));
    }

    @Override
    public Short getShort(String shortId) {
        return resultOrThrow( impl.getShort(shortId));
    }

    @Override
    public List<String> getShorts(String userId) {
        return resultOrThrow( impl.getShorts(userId));
    }

    @Override
    public void follow(String userId1, String userId2, boolean isFollowing, String password) {
        resultOrThrow(impl.follow(userId1, userId2, isFollowing, password));
    }

    @Override
    public List<String> followers(String userId, String password) {
        return resultOrThrow( impl.followers(userId, password));
    }

    @Override
    public void like(String shortId, String userId, boolean isLiked, String password) {
        resultOrThrow(impl.like(shortId, userId, isLiked, password));
    }

    @Override
    public List<String> likes(String shortId, String password) {
        return resultOrThrow( impl.likes(shortId, password));
    }

    @Override
    public List<String> getFeed(String userId, String password) {
        return resultOrThrow( impl.getFeed(userId, password));
    }

    /**
     * Given a Result<T>, either returns the value, or throws the JAX-WS Exception
     * matching the error code...
     */
    protected <T> T resultOrThrow(Result<T> result) {
        if (result.isOK())
            return result.value();
        else
            throw new WebApplicationException(statusCodeFrom(result));
    }

    /**
     * Translates a Result<T> to a HTTP Status code
     */
    private static Response.Status statusCodeFrom(Result<?> result) {
        return switch (result.error()) {
            case CONFLICT -> Response.Status.CONFLICT;
            case NOT_FOUND -> Response.Status.NOT_FOUND;
            case FORBIDDEN -> Response.Status.FORBIDDEN;
            case BAD_REQUEST -> Response.Status.BAD_REQUEST;
            case INTERNAL_ERROR -> Response.Status.INTERNAL_SERVER_ERROR;
            case NOT_IMPLEMENTED -> Response.Status.NOT_IMPLEMENTED;
            case OK -> result.value() == null ? Response.Status.NO_CONTENT : Response.Status.OK;
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };
    }
}
