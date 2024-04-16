package tukano.impl.grpc.servers;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import tukano.api.java.Blobs;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.java.JavaBlobs;


public class GrpcBlobsServerStub implements BlobsGrpc.AsyncService, BindableService {

    Blobs impl = new JavaBlobs();

    @Override
    public final ServerServiceDefinition bindService() {
        return BlobsGrpc.bindService(this);
    }
}
