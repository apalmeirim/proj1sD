package tukano.impl.java;

import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;


public class JavaBlobs implements Blobs {

    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        Shorts shorts = ShortsClientFactory.getClients();
        var res = shorts.hasBlobId(blobId);
        if(!res.isOK()) return Result.error(res.error());
        File file = new File(blobId);

        if(file.exists()){
            try{
                byte[] existingBytes = Files.readAllBytes(file.toPath());
                if(Arrays.equals(bytes, existingBytes))
                    return Result.error(Result.ErrorCode.CONFLICT);
                Files.write(file.toPath(), bytes);
            }catch (IOException e) {
                return Result.error(Result.ErrorCode.INTERNAL_ERROR);
            }
        }
        try {
            file.createNewFile();
            Files.write(file.toPath(), bytes);
        } catch (IOException e) {
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
        return Result.ok();
    }

    @Override
    public Result<byte[]> download(String blobId) {
        File file = new File(blobId);
        try {
            if(file.isFile()) return Result.ok(Files.readAllBytes(file.toPath()));
            return Result.error(Result.ErrorCode.NOT_FOUND);
        } catch (IOException e) {
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Void> delete(String blobId) {
        return null;
    }
}
