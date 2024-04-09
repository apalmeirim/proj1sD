package tukano.impl.java;

import tukano.api.java.Blobs;
import tukano.api.java.Result;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JavaBlobs implements Blobs {

    private final Map<String, Blobs> blobs = new HashMap<>();

    private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());
    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        return null;
    }

    @Override
    public Result<byte[]> download(String blobId) {
        return null;
    }
}
