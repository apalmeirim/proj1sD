package tukano.impl.grpc.clients;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf;
import tukano.impl.grpc.generated_java.UsersProtoBuf;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static tukano.api.java.Result.error;
import static tukano.api.java.Result.ok;
import static tukano.impl.grpc.common.DataModelAdaptor.*;

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
        return toJavaResult(() -> {
            var res = stub.getShort(ShortsProtoBuf.GetShortArgs.newBuilder()
                    .setShortId(shortId).build());
            return GrpcShort_to_Short(res.getValue());
        });
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        return toJavaResult(() -> {
            var res = stub.getShorts(ShortsProtoBuf.GetShortsArgs.newBuilder()
                    .setUserId(userId).build());
            List<String> shortIDs = new ArrayList<>();
            shortIDs.addAll(res.getShortIdList());
            return shortIDs;
        });
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



    static <T> Result<T> toJavaResult(Supplier<T> func) {
        try {
            return ok(func.get());
        } catch(StatusRuntimeException sre) {
            var code = sre.getStatus().getCode();
            if( code == Status.Code.UNAVAILABLE || code == Status.Code.DEADLINE_EXCEEDED )
                throw sre;
            return error( statusToErrorCode( sre.getStatus() ) );
        }
    }

    static Result.ErrorCode statusToErrorCode(Status status ) {
        return switch( status.getCode() ) {
            case OK -> Result.ErrorCode.OK;
            case NOT_FOUND -> Result.ErrorCode.NOT_FOUND;
            case ALREADY_EXISTS -> Result.ErrorCode.CONFLICT;
            case PERMISSION_DENIED -> Result.ErrorCode.FORBIDDEN;
            case INVALID_ARGUMENT -> Result.ErrorCode.BAD_REQUEST;
            case UNIMPLEMENTED -> Result.ErrorCode.NOT_IMPLEMENTED;
            default -> Result.ErrorCode.INTERNAL_ERROR;
        };
    }
}
