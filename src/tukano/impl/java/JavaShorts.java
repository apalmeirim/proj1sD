 package tukano.impl.java;

import tukano.api.Follow;
import tukano.api.Likes;
import tukano.api.Short;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

import java.util.*;
import java.util.logging.Logger;

public class JavaShorts implements Shorts {

    private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

    @Override
    public Result<Short> createShort(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var resUser = users.getUser(userId, password);
        if (!resUser.isOK()) return Result.error(resUser.error());
        String blob = Discovery.getInstance().knownUrisOf("blobs", 1)[0].toString();
        UUID blobsId = UUID.randomUUID();
        Short s = new Short("shortID_" + UUID.randomUUID(), userId, blob + "/blobs/" + blobsId);
        Hibernate.getInstance().persist(s);
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
        var res = Hibernate.getInstance().sql("SELECT * FROM Short WHERE shortId LIKE '"+ shortId +"'", Short.class);
        if (res.isEmpty())
            return Result.error(Result.ErrorCode.NOT_FOUND);
        return Result.ok(res.get(0));
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        Users users = UsersClientFactory.getClients();
        var res = users.searchUsers(userId);
        if(res.equals(Result.error(Result.ErrorCode.BAD_REQUEST))) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        List<String> shortsIDs = Hibernate.getInstance().sql("SELECT shortId FROM Short WHERE ownerId LIKE '"+ userId + "'", String.class);
        return Result.ok(shortsIDs);
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        if(!checkPwd(userId1, password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        Users users = UsersClientFactory.getClients();
        var resUser1 = users.searchUsers(userId1);
        var resUser2 = users.searchUsers(userId2);
        if(resUser1.equals(Result.error(Result.ErrorCode.BAD_REQUEST)) || resUser2.equals(Result.error(Result.ErrorCode.BAD_REQUEST)))
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        List<Follow> res = Hibernate.getInstance().sql("SELECT * FROM Follow WHERE followerUser LIKE '"+ userId1 +"' AND followedUser LIKE '"+ userId2 +"'", Follow.class);
        if(!isFollowing && !res.isEmpty()) {
            Follow follow = res.get(0);
            Hibernate.getInstance().delete(follow);
        }
        else if(isFollowing) {
            if (!res.isEmpty()) return Result.error(Result.ErrorCode.CONFLICT);
            else Hibernate.getInstance().persist(new Follow(userId2, userId1));
        }
        return Result.ok();
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, password);
        if(!res.isOK())
            return Result.error(res.error());
        List<String> followers = Hibernate.getInstance().sql("SELECT followerUser FROM Follow WHERE followedUser LIKE '"+ userId +"'", String.class);
        return Result.ok(followers);
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        if(shortId == null || userId == null || password == null)
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        if(!checkPwd(userId,password))
            return Result.error(Result.ErrorCode.FORBIDDEN);
        var res = getShort(shortId);
        var resliked = Hibernate.getInstance().sql("SELECT * FROM Likes WHERE shortId LIKE '"+ shortId +"' " +
                "AND userId LIKE '"+ userId +"'", Likes.class);
        if( !res.isOK() || (!isLiked && resliked.isEmpty()))
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if(isLiked && !resliked.isEmpty())
            return Result.error(Result.ErrorCode.CONFLICT);
        Short s = res.value();
        if(isLiked) {
            Hibernate.getInstance().persist(new Likes(shortId,userId));
            s.setTotalLikes(s.getTotalLikes() + 1);
            Hibernate.getInstance().update(s);
        }
        else {
            Likes l = resliked.get(0);
            Hibernate.getInstance().delete(l);
            s.setTotalLikes(s.getTotalLikes() - 1);
            Hibernate.getInstance().update(s);
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
        List<String> likes = Hibernate.getInstance().sql("SELECT userId FROM Likes WHERE shortId LIKE '" + shortId + "'", String.class);
        return Result.ok(likes);
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        Users users = UsersClientFactory.getClients();
        var res = users.getUser(userId, password);
        if(!res.isOK())
            return Result.error(res.error());
        List<Short> feedShorts = new ArrayList<>();
        List<String> following = Hibernate.getInstance().sql("SELECT followedUser FROM Follow WHERE followerUser LIKE '" + userId + "'", String.class);
        Iterator<String> followingIt = following.iterator();
        while(followingIt.hasNext()) {
            List<String> shorts = Hibernate.getInstance().sql("SELECT shortId FROM Short WHERE ownerId LIKE '"+ followingIt.next() +"'", String.class);
            Iterator<String> shortsIt = shorts.iterator();
            while(shortsIt.hasNext()) {
                feedShorts.add(getShort(shortsIt.next()).value());
            }
        }
        Iterator<String> usersShortsIt = Hibernate.getInstance().sql("SELECT shortId FROM Short WHERE ownerId LIKE '"+ userId +"' ORDER BY timestamp DESC", String.class).iterator();
        while(usersShortsIt.hasNext()) {
            feedShorts.add(getShort(usersShortsIt.next()).value());
        }
        feedShorts.sort(Comparator.comparingLong(Short::getTimestamp).reversed());
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


    public Result<String> hasBlobId (String blobId) {
        List<Short> shorts = Hibernate.getInstance().sql("SELECT * FROM Short", Short.class);
        Iterator<Short> it = shorts.iterator();
        while(it.hasNext()){
            Short s = it.next();
            if(s.getBlobUrl().toLowerCase().contains(blobId.toLowerCase())){
                return Result.ok(s.getBlobUrl());
            }
        }
        return Result.error(Result.ErrorCode.FORBIDDEN);
    }
}
