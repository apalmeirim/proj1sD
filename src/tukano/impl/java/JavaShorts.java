package tukano.impl.java;

import tukano.api.Short;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

import java.util.*;
import java.util.logging.Logger;

public class JavaShorts implements Shorts {
    //private final Map<String, Short> shortsID = new HashMap<>();
    //private final Map<String, List<String>> shortsUser = new HashMap<>();
    //private final Map<String, List<String>> followers = new HashMap<>();
    //private final Map<String, List<String>> following = new HashMap<>();
    //private final Map<String, List<String>> likes = new HashMap<>();

    // cada vez que criamos um short temos de associar um blobId a ele
    //private final Map<String, Short> blobIDs = new HashMap<>();

    private static Logger Log = Logger.getLogger(JavaShorts.class.getName());
    private int shortsIdGenerator = 1;

    @Override
    public Result<Short> createShort(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var resUser = users.getUser(userId, password);
        if (resUser.equals(Result.error( Result.ErrorCode.NOT_FOUND)))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if (resUser.equals(Result.error( Result.ErrorCode.FORBIDDEN)))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        if (resUser.equals(Result.error( Result.ErrorCode.BAD_REQUEST)))
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        Short s = new Short("shortID_" + shortsIdGenerator, userId, "blobURL" + shortsIdGenerator);
        // quando fizermos os blobs alterar os sets!!!
        Hibernate.getInstance().persist(s);
        //Blobs blobs = BlobsClientFactory.getClients();
        //var resBlobs = blobs.upload();
        //adicionar nos dos blobs
        Log.info("createShort: shortID_" + shortsIdGenerator++);
        return Result.ok(s);
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        var res = getShort(shortId);
        if (!res.isOK())
            return Result.error(res.error());
        Short s = res.value();
        if(!checkPwd(s.getOwnerId(), password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        Log.info("deleteShort : shortId = " + shortId);
        shortsID.remove(shortId);
        likes.remove(shortId);
        shortsUser.get(s.getOwnerId()).remove(shortId);
        //falta remover no dos blobs
        blobIDs.remove(s.getBlobUrl());
        return Result.ok();
    }

    @Override
    public Result<Short> getShort(String shortId) {
        var res = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId LIKE '"+ shortId +"'", Short.class);
        if (res.isEmpty())
            return Result.error(Result.ErrorCode.NOT_FOUND);
        return Result.ok(res.get(0));
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        List<Short> shortsIDs = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.ownerId LIKE '"+ userId +"'", Short.class);
        if (shortsIDs.isEmpty())
            return Result.error(Result.ErrorCode.NOT_FOUND);
        Iterator<String> it = shortsIDs.iterator();
        List<String> allShorts = new ArrayList<>();
        while(it.hasNext()){
            Short s = it.next();
            allShorts.add(s.getShortId() + ", " + s.getTotalLikes());
        }
        return Result.ok(shortsIDs);
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        if(!checkPwd(userId1, password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        if(!(shortsUser.containsKey(userId1) && shortsUser.containsKey(userId2)))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(!isFollowing) {
            followers.get(userId2).add(userId1);
            following.get(userId1).add(userId2);
        }
        else {
            followers.get(userId2).remove(userId1);
            following.get(userId1).remove(userId2);
        }
        return Result.ok();
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, password);
        if(!res.isOK())
            return Result.error(res.error());
        User user = res.value();
        return Result.ok(user.getFollowers());
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        if(shortId == null || userId == null || password == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        if(!checkPwd(userId,password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        var res = getShort(shortId);
        if( !res.isOK() || (!isLiked && !res.value().getLikes().contains(userId)))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(isLiked && res.value().getLikes().contains(userId))
            return Result.error(Result.ErrorCode.CONFLICT);
        Short s = res.value();
        if(isLiked) {
            s.addLike(userId);
            s.setTotalLikes(s.getTotalLikes() + 1);
        }
        else {
            s.removeLike(userId);
            s.setTotalLikes(s.getTotalLikes() - 1);
        }
        return Result.ok();
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        Short s = shortsID.get(shortId);
        if(s == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(!checkPwd(s.getOwnerId(), password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        return Result.ok(likes.get(shortId));
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        if(shortsUser.containsKey(userId))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(!checkPwd(userId,password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        List<Short> feedShorts = new ArrayList<>();
        Iterator<String> it = following.get(userId).iterator();
        while(it.hasNext()){
            String itID = it.next();
            Iterator<String> itShorts = shortsUser.get(itID).iterator();
            while(itShorts.hasNext())
                feedShorts.add(shortsID.get(itShorts.next()));
        }
        feedShorts.sort(Comparator.comparingLong(Short::getTimestamp));
        List<String> feed = new ArrayList<>();
        Iterator<Short> itString = feedShorts.iterator();
        while(itString.hasNext()) feed.add(itString.next().getShortId());
        return Result.ok(feed);
    }

    /**
     * Method to check the password with the given userId and password, to avoid repeated code.
     * @param userId id of user
     * @param pwd password to check
     * @return true if the password is correct, false if the password is false.
     */
    private boolean checkPwd(String userId, String pwd) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, pwd);
        if(res.equals(Result.error(Result.ErrorCode.FORBIDDEN)))
            return false;
        return true;
    }


    public String getShortIDFromBlob(String blobId) {
        return null;//blobIDs.get(blobId).getShortId();
    }
}
