package tukano.impl.grpc.servers;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf;
import tukano.impl.java.JavaShorts;

import static tukano.impl.grpc.common.DataModelAdaptor.*;


public class GrpcShortsServerStub implements ShortsGrpc.AsyncService, BindableService {

    Shorts impl = new JavaShorts();

    @Override
    public final ServerServiceDefinition bindService() {
        return ShortsGrpc.bindService(this);
    }

    @Override
    public void createShort(ShortsProtoBuf.CreateShortArgs request, StreamObserver<ShortsProtoBuf.CreateShortResult> responseObserver) {
        var res = impl.createShort(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.CreateShortResult.newBuilder().setValue(Short_to_GrpcShort(res.value())).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteShort(ShortsProtoBuf.DeleteShortArgs request, StreamObserver<ShortsProtoBuf.DeleteShortResult> responseObserver) {
        var res = impl.deleteShort(request.getShortId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.DeleteShortResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getShort(ShortsProtoBuf.GetShortArgs request, StreamObserver<ShortsProtoBuf.GetShortResult> responseObserver) {
        var res = impl.getShort(request.getShortId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.GetShortResult.newBuilder().setValue(Short_to_GrpcShort(res.value())).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getShorts(ShortsProtoBuf.GetShortsArgs request, StreamObserver<ShortsProtoBuf.GetShortsResult> responseObserver) {
        var res = impl.getShorts(request.getUserId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.GetShortsResult.newBuilder().addAllShortId(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void follow(ShortsProtoBuf.FollowArgs request, StreamObserver<ShortsProtoBuf.FollowResult> responseObserver) {
        var res = impl.follow(request.getUserId1(), request.getUserId2(), request.getIsFollowing(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.FollowResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void followers(ShortsProtoBuf.FollowersArgs request, StreamObserver<ShortsProtoBuf.FollowersResult> responseObserver) {
        var res = impl.followers(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.FollowersResult.newBuilder().addAllUserId(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void like(ShortsProtoBuf.LikeArgs request, StreamObserver<ShortsProtoBuf.LikeResult> responseObserver) {
        var res = impl.like(request.getShortId(), request.getUserId(), request.getIsLiked(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.LikeResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void likes(ShortsProtoBuf.LikesArgs request, StreamObserver<ShortsProtoBuf.LikesResult> responseObserver) {
        var res = impl.likes(request.getShortId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.LikesResult.newBuilder().addAllUserId(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getFeed(ShortsProtoBuf.GetFeedArgs request, StreamObserver<ShortsProtoBuf.GetFeedResult> responseObserver) {
        var res = impl.getFeed(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext( ShortsProtoBuf.GetFeedResult.newBuilder().addAllShortId(res.value()).build());
            responseObserver.onCompleted();
        }
    }

    protected static Throwable errorCodeToStatus( Result.ErrorCode error ) {
        var status =  switch( error) {
            case NOT_FOUND -> io.grpc.Status.NOT_FOUND;
            case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
            case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
            case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
            case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
            default -> io.grpc.Status.INTERNAL;
        };

        return status.asException();
    }
}
