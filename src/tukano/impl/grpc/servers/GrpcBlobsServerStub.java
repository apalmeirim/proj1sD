package tukano.impl.grpc.servers;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.api.java.Blobs;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf;
import tukano.impl.java.JavaBlobs;

import static tukano.impl.grpc.common.DataModelAdaptor.Short_to_GrpcShort;


public class GrpcBlobsServerStub implements BlobsGrpc.AsyncService, BindableService {

    Blobs impl = new JavaBlobs();

    @Override
    public final ServerServiceDefinition bindService() {
        return BlobsGrpc.bindService(this);
    }
}
