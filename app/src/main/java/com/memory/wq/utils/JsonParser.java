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
import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.EventType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private final static String TAG = JsonParser.class.getName();

    public static UserInfo registerParser(JSONObject json) {
        UserInfo userInfo = new UserInfo();
        try {
            JSONObject data = json.getJSONObject("data");
            String token = data.getString("token");
            String userId = data.getString("userId");
            String dataEmail = data.getString("email");
            String userName = data.getString("username");
            String avatarUrl = data.optString("avatarUrl");
            long uuNumber = data.getLong("uuNumber");

            userInfo.setEmail(dataEmail);
            userInfo.setToken(token);
            userInfo.setId(userId);
            userInfo.setUserName(userName);
            userInfo.setAvatarPath(avatarUrl);
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
            String token = data.getString("token");
            String userId = data.getString("uuid");
            String dataEmail = data.getString("email");
            String userName = data.getString("name");
            String avatarUrl = data.optString("avatarUrl");
            long uuNumber = data.getLong("uuNumber");

            userInfo.setEmail(dataEmail);
            userInfo.setToken(token);
            userInfo.setId(userId);
            userInfo.setUserName(userName);
            userInfo.setAvatarPath(avatarUrl);
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


    public static List<FriendRelaInfo> friendRelaParser(JSONArray requestList) {
        List<FriendRelaInfo> friendRelaList = new ArrayList<>();
        try {
            for (int i = 0; i < requestList.length(); i++) {
                JSONObject item = requestList.getJSONObject(i);
                FriendRelaInfo friendReqInfo = parseFriendRequest(item);
                Log.d(TAG, "friendRelaParser: ===================friendReqInfo" + friendReqInfo);
                friendRelaList.add(friendReqInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendRelaList;
    }

    private static FriendRelaInfo parseFriendRequest(JSONObject item) throws JSONException {
        FriendRelaInfo info = new FriendRelaInfo();
        info.setId(item.optInt("serverId"));
        info.setSourceEmail(item.optString("sourceEmail"));
        info.setTargetEmail(item.optString("targetEmail"));

        info.setSourceNickname(item.optString("sourceNickname"));
        info.setTargetNickname(item.optString("targetNickname"));

        info.setSourceAvatarUrl(item.optString("sourceAvatarUrl"));
        info.setTargetAvatarUrl(item.optString("targetAvatarUrl"));

        info.setValidMsg(item.optString("validMsg"));
        String updateAt = item.optString("updateAt");
        long stamp = TimeUtils.stringTime2Stamp(updateAt);
        info.setUpdateAt(stamp);
        info.setState(item.optString("status"));
        return info;
    }

    public static EventType getJsonType(String message) {
        EventType type = EventType.UNKNOWN;
        try {
            JSONObject json = new JSONObject(message);
            String eventType = json.getString("event_type");
            type = EventType.fromString(eventType);
            Log.d(TAG, "===getJsonType: type" + type);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "===getJsonType:出异常了 " + e.getMessage());
        }
        return type;
    }

    public static FriendInfo searchFriendParser(JSONObject json) {
        String avatarUrl = null;
        String email = null;
        String username = null;
        try {
            JSONObject data = json.getJSONObject("data");
            avatarUrl = data.getString("avatarUrl");
            email = data.getString("email");
            username = data.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setAvatarUrl(avatarUrl != null ? avatarUrl : "");
        friendInfo.setEmail(email);
        friendInfo.setNickname(username);
        System.out.println("==============searchFriendParser:" + friendInfo.toString());

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
        friendInfo.setNickname(item.getString("userName"));
        friendInfo.setEmail(item.getString("email"));
        friendInfo.setAvatarUrl(item.getString("avatarUrl"));
        friendInfo.setUpdateAt(TimeUtils.stringTime2Stamp(item.getString("updateAt")));
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
        String sessionId = item.getString("sessionId");
        String senderEmail = item.getString("senderEmail");
        String receiverEmail = item.getString("receiverEmail");
        String content = item.getString("content");

        msgInfo.setContent(content);
        msgInfo.setReceiverEmail(receiverEmail);
        msgInfo.setSenderEmail(senderEmail);


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
        String senderEmail = item.getString("senderEmail");
        String linkImageUrl = item.getString("linkImageUrl");
        String receiverEmail = item.getString("receiverEmail");
        String linkContent = item.getString("linkContent");

        msgInfo.setMsgType(1);
        msgInfo.setLinkTitle(linkTitle);
        msgInfo.setLinkContent(linkContent);
        msgInfo.setLinkImageUrl(linkImageUrl);
        msgInfo.setSenderEmail(senderEmail);
        msgInfo.setReceiverEmail(receiverEmail);

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
        PostInfo postInfo = new PostInfo();
        postInfo.setPostId(item.getInt("postId"));
        postInfo.setPoster(item.getString("userId"));
        String coverUrl = item.getString("coverUrl");
        if ("null".equals(coverUrl))
            coverUrl = null;
        postInfo.setCommentCoverUrl(coverUrl);

        postInfo.setLikeCount(item.getInt("likeCount"));
        postInfo.setTimestamp(item.getLong("createAt"));

        String textContent = item.getString("content");
        if (TextUtils.isEmpty(textContent)) {
            Log.e(TAG, "===postInfoParser: content is empty");
            postInfo.setContent("null了");
            postInfo.setTitle("null了");
            return postInfo;
        }

        JSONObject textContentJson;
        try {
            textContentJson = new JSONObject(textContent);
        } catch (Exception e) {
            postInfo.setContent("null了");
            postInfo.setTitle("null了");
            return postInfo;
        }

        String content = textContentJson.getString("content");
        String title = textContentJson.getString("title");
        postInfo.setContent(content);
        postInfo.setTitle(title);

        List<String> imageUrlList = new ArrayList<>();
        JSONArray imageUrlsArray = item.getJSONArray("imageUrls");
        if (imageUrlsArray == null) {
            postInfo.setContentImagesUrlList(imageUrlList);
            Log.d(TAG, "===[x] postInfoParser #396");
            return postInfo;
        }

        for (int i = 0; i < imageUrlsArray.length(); i++) {
            imageUrlList.add(imageUrlsArray.getString(i));
        }
        postInfo.setContentImagesUrlList(imageUrlList);
        return postInfo;
    }

    public static List<PostCommentInfo> commentParser(JSONObject json) {

        return null;
    }
    private static PostCommentInfo parseCommentInfo(JSONObject item){

        return null;
    }
}
