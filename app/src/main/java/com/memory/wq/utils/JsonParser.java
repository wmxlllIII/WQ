package com.memory.wq.utils;

import android.text.TextUtils;
import android.util.Log;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.beans.RtcInfo;
import com.memory.wq.beans.StsTokenInfo;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.enumertions.EventType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private final static String TAG = "WQ_JsonParser";

    public static UserInfo registerParser(JSONObject json) {
        UserInfo userInfo = new UserInfo();
        try {
            JSONObject data = json.getJSONObject("data");
            String token = data.getString("token");
            String dataEmail = data.getString("email");
            String userName = data.getString("username");
            String avatarUrl = data.optString("avatarUrl");
            long uuNumber = data.getLong("uuNumber");

            userInfo.setEmail(dataEmail);
            userInfo.setToken(token);
            userInfo.setUserName(userName);
            userInfo.setAvatarUrl(avatarUrl);
            userInfo.setUuNumber(uuNumber);
            Log.d(TAG, "registerParser: ===解析信息" + userInfo.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public static UserInfo loginParser(JSONObject json) {
        UserInfo userInfo = new UserInfo();
        try {
            JSONObject data = json.getJSONObject("data");
            String avatarUrl = data.optString("avatarUrl");
            String dataEmail = data.getString("email");
            String userName = data.getString("name");
            String token = data.getString("token");
            long uuNumber = data.getLong("uuNumber");

            userInfo.setEmail(dataEmail);
            userInfo.setToken(token);
            userInfo.setUserName(userName);
            userInfo.setAvatarUrl(avatarUrl);
            userInfo.setUuNumber(uuNumber);

            System.out.println("================2userinfoTostring" + userInfo.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return userInfo;
    }

    public static String avatarParse(JSONObject json) {
        String avatar = "";
        try {
            int code = json.getInt("code");
            if (code != 1) {
                return "";
            }
            avatar = json.getString("data");
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "解析异常: " + e.getMessage());
            e.printStackTrace();
        }
        return avatar;
    }


    public static List<FriendRelaInfo> friReqParser(JSONArray requestList) {
        List<FriendRelaInfo> friendRelaList = new ArrayList<>();
        try {
            for (int i = 0; i < requestList.length(); i++) {
                JSONObject item = requestList.getJSONObject(i);
                FriendRelaInfo friendReqInfo = parseFriReq(item);
                Log.d(TAG, "friendRelaParser: ===================friendReqInfo" + friendReqInfo);
                friendRelaList.add(friendReqInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendRelaList;
    }

    private static FriendRelaInfo parseFriReq(JSONObject item) throws JSONException {
        FriendRelaInfo info = new FriendRelaInfo();
        info.setRelaId(item.optInt("serverId"));
        info.setSourceId(item.optLong("sourceId"));
        info.setTargetId(item.optLong("targetId"));

        info.setValidMsg(item.optString("validMsg"));
        long createAt = item.optLong("createAt");
        long updateAt = item.optLong("updateAt");
        info.setCreateAt(createAt);
        info.setUpdateAt(updateAt);
        info.setState(item.optString("status"));
        return info;
    }

    public static List<FriendInfo> friParser(JSONObject friRespJson) {
        List<FriendInfo> friendList = new ArrayList<>();
        FriendInfo friend = null;
        try {
            friend = friendInfoParser(friRespJson);

        } catch (JSONException e) {
            Log.d(TAG, "[x] friRespParser #131 " + e.getMessage());
        }
        friendList.add(friend);
        return friendList;
    }

    public static FriendRelaInfo friRelaParser(JSONObject friRespJson) {
        FriendRelaInfo friendRela = null;
        try {
            friendRela = parseFriReq(friRespJson);

        } catch (JSONException e) {
            Log.d(TAG, "[x] friRelaParser #152 " + e.getMessage());
        }
        return friendRela;
    }

    public static EventType getJsonType(String message) {
        return EventType.fromString(message);
    }

    public static FriendInfo searchFriendParser(JSONObject json) {
        FriendInfo friendInfo = new FriendInfo();
        try {
            JSONObject data = json.getJSONObject("data");
            String avatarUrl = data.getString("avatarUrl");
            String email = data.getString("email");
            String username = data.getString("username");
            long uuNumber = data.getLong("uuNumber");

            friendInfo.setAvatarUrl(avatarUrl);
            friendInfo.setEmail(email);
            friendInfo.setNickname(username);
            friendInfo.setUuNumber(uuNumber);
        } catch (JSONException e) {
            Log.d(TAG, "[x] searchFriendParser #148" + e.getMessage());
        }

        Log.d(TAG, "searchFriendParser: friendInfo" + friendInfo);
        return friendInfo;
    }

    public static List<FriendInfo> friendInfoListParser(JSONArray jsonArray) {
        List<FriendInfo> friendList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                FriendInfo friendInfo = friendInfoParser(item);
                friendList.add(friendInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendList;
    }

    private static FriendInfo friendInfoParser(JSONObject item) throws JSONException {
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setUuNumber(item.getLong("uuNumber"));
        friendInfo.setNickname(item.getString("username"));
        friendInfo.setEmail(item.getString("email"));
        friendInfo.setAvatarUrl(item.getString("avatarUrl"));
//        friendInfo.setUpdateAt(TimeUtils.stringTime2Stamp(item.getString("updateAt")));
        return friendInfo;
    }

    public static List<MsgInfo> msgParser(JSONArray msgInfoList) {
        List<MsgInfo> msgList = new ArrayList<>();
        try {
            for (int i = 0; i < msgInfoList.length(); i++) {
                JSONObject item = msgInfoList.getJSONObject(i);
                MsgInfo msgInfo = parseMsg(item);
                msgList.add(msgInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgList;
    }

    private static MsgInfo parseMsg(JSONObject item) throws JSONException {
        MsgInfo msgInfo = new MsgInfo();
        long senderId = item.getLong("senderId");
        long receiverId = item.getLong("receiverId");
        String content = item.getString("content");
        int type = item.getInt("type");
        int msgId = item.getInt("msgId");
        long createAt = item.getLong("createAt");

        msgInfo.setContent(content);
        msgInfo.setMsgId(msgId);
        msgInfo.setMsgType(ContentType.fromInt(type));
        msgInfo.setReceiverId(receiverId);
        msgInfo.setSenderId(senderId);
        msgInfo.setCreateAt(createAt);

        return msgInfo;
    }

    public static List<MsgInfo> shareMsgParser(JSONArray shareMsgInfoList) {
        List<MsgInfo> shareMsgList = new ArrayList<>();
        try {
            for (int i = 0; i < shareMsgInfoList.length(); i++) {
                JSONObject item = shareMsgInfoList.getJSONObject(i);
                MsgInfo msgInfo = parseShareMsg(item);
                shareMsgList.add(msgInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shareMsgList;
    }

    private static MsgInfo parseShareMsg(JSONObject item) throws JSONException {
        MsgInfo msgInfo = new MsgInfo();
        String linkTitle = item.getString("linkTitle");
        long senderId = item.getLong("senderId");
        String linkImageUrl = item.getString("linkImageUrl");
        long receiverId = item.getLong("receiverId");
        String linkContent = item.getString("linkContent");

        msgInfo.setMsgType(ContentType.TYPE_LINK);
        msgInfo.setLinkTitle(linkTitle);
        msgInfo.setLinkContent(linkContent);
        msgInfo.setLinkImageUrl(linkImageUrl);
        msgInfo.setSenderId(senderId);
        msgInfo.setReceiverId(receiverId);

        return msgInfo;
    }

    public static List<MovieInfo> movieParser(JSONArray movieList) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        try {
            for (int i = 0; i < movieList.length(); i++) {
                JSONObject item = movieList.getJSONObject(i);
                MovieInfo movieInfo = movieInfoParser(item);
                movieInfoList.add(movieInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieInfoList;
    }

    private static MovieInfo movieInfoParser(JSONObject item) throws JSONException {
        MovieInfo movieInfo = new MovieInfo();
        int movieId = item.getInt("id");
        String movieName = item.getString("movieName");
        String movieUrl = item.getString("movieUrl");
        String movieCover = item.getString("movieCover");
        String movieActors = item.getString("movieActors");
        double movieLength = item.getDouble("movieLength");

        movieInfo.setMovieId(movieId);
        movieInfo.setTitle(movieName);
        movieInfo.setMovieUrl(movieUrl);
        movieInfo.setCoverUrl(movieCover);
        movieInfo.setActors(movieActors);
        movieInfo.setLength(movieLength);
        return movieInfo;
    }

    public static List<RoomInfo> roomParer(JSONArray roomList) {
        List<RoomInfo> roomInfoList = new ArrayList<>();
        try {
            for (int i = 0; i < roomList.length(); i++) {
                JSONObject item = roomList.getJSONObject(i);
                RoomInfo roomInfo = roomInfoParser(item);
                roomInfoList.add(roomInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomInfoList;
    }

    private static RoomInfo roomInfoParser(JSONObject item) throws JSONException {
        RoomInfo roomInfo = new RoomInfo();
        String movieUrl = item.getString("movieUrl");
        String roomId = item.getString("roomId");
        String movieName = item.getString("movieName");
        String movieCover = item.getString("movieCover");

        roomInfo.setMovieName(movieName);
        roomInfo.setMovieCover(movieCover);
        roomInfo.setRoomId(roomId);
        roomInfo.setMovieUrl(movieUrl);
        return roomInfo;
    }

    public static RtcInfo rtcTokenParser(JSONObject json) throws JSONException {
        RtcInfo rtcInfo = new RtcInfo();
        String token = json.getString("token");
        String appId = json.getString("appId");
        String channelName = json.getString("channelName");
        String userId = json.getString("userId");
        int role = json.getInt("role");
        int expireTime = json.getInt("expireTime");
        rtcInfo.setToken(token);
        rtcInfo.setAppId(appId);
        rtcInfo.setChannelName(channelName);
        return rtcInfo;
    }

    public static MsgInfo shareMsgParser(String msg) {
        MsgInfo msgInfo = new MsgInfo();
        try {
            JSONObject json = new JSONObject(msg);
            String linkTitle = json.getString("linkTitle");
            String linkContent = json.getString("linkContent");
            String linkImageUrl = json.getString("linkImageUrl");
            msgInfo.setLinkTitle(linkTitle);
            msgInfo.setLinkContent(linkContent);
            msgInfo.setLinkImageUrl(linkImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgInfo;
    }

    public static PageResult<PostInfo> postParser(JSONObject json) {
        PageResult<PostInfo> pageResult = new PageResult<>();
        try {
            JSONArray postArray = json.getJSONArray("resultList");
            List<PostInfo> postInfoList = new ArrayList<>();

            for (int i = 0; i < postArray.length(); i++) {
                JSONObject item = postArray.getJSONObject(i);
                PostInfo postInfo = postInfoParser(item);
                postInfoList.add(postInfo);
            }

            pageResult.setResultList(postInfoList);
            pageResult.setPage(json.getInt("page"));
            pageResult.setSize(json.getInt("size"));
            pageResult.setHasNext(json.getBoolean("hasNext"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pageResult;
    }

    private static PostInfo postInfoParser(JSONObject item) throws JSONException {
        Log.d(TAG, "postInfoParser: " + item.toString());
        PostInfo postInfo = new PostInfo();
        postInfo.setPostId(item.getInt("postId"));
        postInfo.setPoster(item.getString("userId"));
        String coverUrl = item.getString("coverUrl");
        if ("null".equals(coverUrl)) {
            coverUrl = null;
        }

        postInfo.setCommentCoverUrl(coverUrl);
        String userAvatarUrl = item.getString("userAvatarUrl");
        postInfo.setPosterAvatar(userAvatarUrl);
        postInfo.setLikeCount(item.getInt("likeCount"));
        postInfo.setTimestamp(item.getLong("createAt"));
        postInfo.setTitle(item.getString("title"));

        String content = item.getString("content");
        if (TextUtils.isEmpty(content)) {
            Log.e(TAG, "[x] postInfoParser #379");
            postInfo.setContent("[x] postInfoParser #379 content空了");
            postInfo.setTitle("null了");
            return postInfo;
        }

        postInfo.setContent(content);

        List<String> imageUrlList = new ArrayList<>();
        JSONArray imageUrlsArray = item.getJSONArray("imageUrls");

        for (int i = 0; i < imageUrlsArray.length(); i++) {
            imageUrlList.add(imageUrlsArray.getString(i));
        }
        postInfo.setContentImagesUrlList(imageUrlList);
        return postInfo;
    }

    public static List<PostCommentInfo> commentParser(JSONObject json) {
        List<PostCommentInfo> postCommentInfoList = new ArrayList<>();
        try {
            JSONArray resultList = json.getJSONArray("resultList");
            for (int i = 0; i < resultList.length(); i++) {
                JSONObject item = resultList.getJSONObject(i);
                postCommentInfoList.add(parseCommentInfo(item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postCommentInfoList;
    }

    private static PostCommentInfo parseCommentInfo(JSONObject item) throws JSONException {
        PostCommentInfo comment = new PostCommentInfo();
        comment.setCommentId(item.getInt("id"));
        comment.setPostId(item.getInt("postId"));
        comment.setParentId(item.optInt("parentId", -1));
        comment.setUserId(item.optString("userId"));
        comment.setUserName(item.optString("userName"));
        comment.setReplyToUserId(item.optString("replyToUserId"));
        comment.setReplyToUserName(item.optString("replyToUserName"));
        comment.setContent(item.optString("content"));
        comment.setTimestamp(item.optLong("createAt"));
        comment.setExpanded(false);

        JSONArray childArray = item.optJSONArray("childCommentList");
        if (childArray != null && childArray.length() > 0) {
            List<PostCommentInfo> childList = new ArrayList<>();
            for (int i = 0; i < childArray.length(); i++) {
                JSONObject childItem = childArray.getJSONObject(i);
                childList.add(parseCommentInfo(childItem));
            }
            comment.setChildCommentList(childList);
        }

        return comment;
    }

    public static StsTokenInfo stsTokenParser(JSONObject json) {
        StsTokenInfo stsTokenInfo = new StsTokenInfo();
        try {
            JSONObject dataJson = json.getJSONObject("data");

            stsTokenInfo.setAccessKeyId(dataJson.getString("accessKeyId"));
            stsTokenInfo.setAccessKeySecret(dataJson.getString("accessKeySecret"));
            stsTokenInfo.setSecurityToken(dataJson.getString("securityToken"));
            stsTokenInfo.setEndPoint(dataJson.getString("endpoint"));
            stsTokenInfo.setRegion(dataJson.getString("region"));
            stsTokenInfo.setBucketName(dataJson.getString("bucketName"));

        } catch (JSONException e) {
            Log.e(TAG, "[x] stsTokenParser #466 " + e.getMessage());
        }
        return stsTokenInfo;
    }
}
