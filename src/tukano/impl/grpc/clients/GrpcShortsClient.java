package tukano.impl.grpc.clients;

import io.grpc.ManagedChannelBuilder;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrpcShortsClient implements Shorts {
    private static final long GRPC_REQUEST_TIMEOUT = 5000;
        final ShortsGrpc.ShortsBlockingStub stub;
    public GrpcShortsClient(URI serverURI) {
        var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
        stub = ShortsGrpc.newBlockingStub( channel ).withDeadlineAfter(GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
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
    public Result<String> hasBlobId( String blobId) {
        return null;
    }
}
