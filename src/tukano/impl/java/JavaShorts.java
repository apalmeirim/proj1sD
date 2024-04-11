package tukano.impl.java;

import tukano.api.Short;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;

import java.util.*;
import java.util.logging.Logger;

public class JavaShorts implements Shorts {
    private final Map<String, Short> shortsID = new HashMap<>();
    private final Map<String, List<String>> shortsUser = new HashMap<>();
    private final Map<String, List<String>> followers = new HashMap<>();
    private final Map<String, List<String>> following = new HashMap<>();
    private final Map<String, List<String>> likes = new HashMap<>();

    // cada vez que criamos um short temos de associar um blobId a ele
    private final Map<String, Short> blobIDs = new HashMap<>();

    private static Logger Log = Logger.getLogger(JavaShorts.class.getName());
    private int shortsIdGenerator = 1;

    @Override
    public Result<Short> createShort(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, password);
        if (res.equals(Result.error( Result.ErrorCode.NOT_FOUND)))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if (resUser.equals(Result.error( Result.ErrorCode.FORBIDDEN)))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        if (resUser.equals(Result.error( Result.ErrorCode.BAD_REQUEST)))
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        Short s = new Short("ID_" + shortsIdGenerator, userId, "ID_" + shortsIdGenerator);
        // quando fizermos os blobs alterar os sets!!!
        shortsID.put(s.getShortId(),s);
        shortsUser.get(userId).add(s.getShortId());
        likes.put(s.getShortId(),new ArrayList<>());
        // blobIDs.put("ID_" + shortsIdGenerator, s);
        Log.info("createShort: ID_" + shortsIdGenerator++);
        return Result.ok(s);
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        Short s = shortsID.get(shortId);
        if(!checkPwd(s.getOwnerId(), password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        if (!shortsID.containsKey(shortId))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        Log.info("deleteShort : shortId = " + shortId);
        shortsID.remove(shortId);
        likes.remove(shortId);
        shortsUser.get(s.getOwnerId()).remove(shortId);
        return Result.ok();
    }

    @Override
    public Result<Short> getShort(String shortId) {
        Short s = shortsID.get(shortId);
        if (s == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);
        return Result.ok(s);
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        List<String> shortsIDs = shortsUser.get(userId);
        if (shortsIDs == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);
        Iterator<String> it = shortsIDs.iterator();
        List<String> allShorts = new ArrayList<>();
        while(it.hasNext()){
            Short s = shortsID.get(it.next());
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
        if(!shortsUser.containsKey(userId))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(!checkPwd(userId,password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        return Result.ok(followers.get(userId));
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        if(shortId == null || userId == null || password == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        if(!checkPwd(userId,password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        if(!(likes.containsKey(shortId)) || (!isLiked && !likes.get(shortId).contains(userId)))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(isLiked && likes.get(shortId).contains(userId))
            return Result.error(Result.ErrorCode.CONFLICT);
        Short s = shortsID.get(shortId);
        if(isLiked) {
            likes.get(shortId).add(userId);
            s.setTotalLikes(s.getTotalLikes() + 1);
        }
        else {
            likes.get(shortId).remove(userId);
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
        return blobIDs.get(blobId).getShortId();
    }
}
