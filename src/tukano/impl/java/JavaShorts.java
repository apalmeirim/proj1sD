package tukano.impl.java;

import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

import java.lang.reflect.Array;
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
        Hibernate.getInstance().delete(s);
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
        Iterator<Short> it = shortsIDs.iterator();
        List<String> allShorts = new ArrayList<>();
        while(it.hasNext()){
            Short s = it.next();
            allShorts.add(s.getShortId() + ", " + s.getTotalLikes());
        }
        return Result.ok(allShorts);
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        if(!checkPwd(userId1, password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        var res1 = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId LIKE '"+ userId1 +"'", User.class);
        var res2 = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId LIKE '"+ userId2 +"'", User.class);
        if(res1.isEmpty() || res2.isEmpty())
            return Result.error(Result.ErrorCode.NOT_FOUND);
        User user1 = res1.get(0);
        User user2 = res2.get(0);
        if(!isFollowing) {
            user1.addFollowing(user2.getUserId());
            user2.addFollower(user1.getUserId());
        }
        else {
            user1.removeFollowing(user2.getUserId());
            user2.removeFollower(user1.getUserId());
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
        var res = getShort(shortId);
        if(!res.isOK())
            return Result.error(res.error());
        Short s = res.value();
        Users users = UsersClientFactory.getClients();
        var resUser = users.getUser(s.getOwnerId(), password);
        if(!resUser.isOK())
            return Result.error(resUser.error());
        return Result.ok(s.getLikes());
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, password);
        if(!res.isOK())
            return Result.error(res.error());
        User user = res.value();
        List<Short> feedShorts = new ArrayList<>();
        List<String> following = user.getFollowing();
        Iterator<String> followingIt = following.iterator();
        while(followingIt.hasNext()) {
            List<String> shorts = getShorts(followingIt.next()).value();
            Iterator<String> shortsIt = shorts.iterator();
            while(shortsIt.hasNext()) {
                feedShorts.add(getShort(shortsIt.next()).value());
            }
        }
        Iterator<String> usersShortsIt = getShorts(userId).value().iterator();
        while(usersShortsIt.hasNext()) {
            feedShorts.add(getShort(usersShortsIt.next()).value());
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
