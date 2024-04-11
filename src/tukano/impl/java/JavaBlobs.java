package tukano.impl.java;

import tukano.api.java.Blobs;
import tukano.api.java.Result;


import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.logging.Logger;

public class JavaBlobs implements Blobs {

    private final Map<String, byte[]> blobs = new HashMap<>();
    private final Map<String, String> shortsToBlobID = new HashMap<>();

    private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());

    // falta fazer a comunicacao com os shorts
    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        blobs.put(blobId, bytes);
        shortsToBlobID.put(blobId, shorts.getShortIDFromBlob(blobId));
        return Result.ok();
    }

    @Override
    public Result<byte[]> download(String blobId) {
        byte[] bytes = blobs.get(blobId);
        if(bytes == null) return Result.error(Result.ErrorCode.NOT_FOUND);
        return Result.ok(bytes);
    }


    public Result<Void> delete(String blobId) {
        shortsToBlobID.remove(blobId);
        blobs.remove(blobId);
    }

}
