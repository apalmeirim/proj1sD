package tukano.impl.grpc.servers;



import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import org.hibernate.sql.Delete;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.CreateShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.CreateShortResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.DeleteShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.DeleteShortResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortsArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortsResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowersArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowersResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikeArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikeResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikesArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikesResult;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetFeedArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetFeedResult;
import tukano.impl.java.JavaShorts;

import static tukano.impl.grpc.common.DataModelAdaptor.*;

public class GrpcShortsServerStub implements ShortsGrpc.AsyncService, BindableService{

    Shorts impl = new JavaShorts();

    @Override
    public final ServerServiceDefinition bindService() {
        return ShortsGrpc.bindService(this);
    }

    @Override
    public void createShort(CreateShortArgs request, StreamObserver<CreateShortResult> responseObserver) {
        var res = impl.createShort(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(CreateShortResult.newBuilder().setValue( Short_to_GrpcShort(res.value())).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteShort(DeleteShortArgs request, StreamObserver<DeleteShortResult> responseObserver) {
        var res = impl.deleteShort(request.getShortId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(DeleteShortResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getShort(GetShortArgs request, StreamObserver<GetShortResult> responseObserver) {
        var res = impl.getShort(request.getShortId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(GetShortResult.newBuilder().setValue( Short_to_GrpcShort(res.value())).build());
            responseObserver.onCompleted();
        }
    }
    //isto esta bem?
    @Override
    public void getShorts(GetShortsArgs request, StreamObserver<GetShortsResult> responseObserver) {
        var res = impl.getShorts(request.getUserId());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(GetShortsResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void follow(FollowArgs request, StreamObserver<FollowResult> responseObserver) {
        var res = impl.follow(request.getUserId1(), request.getUserId2(), request.getIsFollowing(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(FollowResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    //isto esta bem?
    @Override
    public void followers(FollowersArgs request, StreamObserver<FollowersResult> responseObserver) {
        var res = impl.followers(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(FollowersResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void like(LikeArgs request, StreamObserver<LikeResult> responseObserver) {
        var res = impl.like(request.getShortId(),request.getUserId(), request.getIsLiked(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(LikeResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    //isto esta bem?
    @Override
    public void likes(LikesArgs request, StreamObserver<LikesResult> responseObserver) {
        var res = impl.likes(request.getShortId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(LikesResult.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    //isto esta bem?
    @Override
    public void getFeed(GetFeedArgs request, StreamObserver<GetFeedResult> responseObserver) {
        var res = impl.getFeed(request.getUserId(), request.getPassword());
        if( ! res.isOK() )
            responseObserver.onError(errorCodeToStatus(res.error()));
        else {
            responseObserver.onNext(GetFeedResult.newBuilder().build());
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
