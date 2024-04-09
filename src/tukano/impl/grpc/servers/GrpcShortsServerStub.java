package tukano.impl.grpc.servers;



import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;

import tukano.impl.java.JavaShorts;

public class GrpcShortsServerStub implements ShortsGrpc.AsyncService, BindableService{

    Shorts impl = new JavaShorts();

    @Override
    public final ServerServiceDefinition bindService() {
        return ShortsGrpc.bindService(this);
    }


}
