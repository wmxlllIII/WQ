package com.memory.wq.utils;

import android.text.TextUtils;
import android.util.Log;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.beans.MovieCateInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.MsgListInfo;
import com.memory.wq.beans.OnlineInfo;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.PostDetailInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.beans.RtcInfo;
import com.memory.wq.beans.StsTokenInfo;
import com.memory.wq.beans.TagInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UserInfo;
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
            userInfo.setUsername(userName);
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
            String username = data.getString("name");
            String token = data.getString("token");
            long uuNumber = data.getLong("uuNumber");

            userInfo.setEmail(dataEmail);
            userInfo.setToken(token);
            userInfo.setUsername(username);
            userInfo.setAvatarUrl(avatarUrl);
            userInfo.setUuNumber(uuNumber);

            Log.d(TAG, "[✓] loginParser #68 " + userInfo);

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
                friendRelaList.add(friendReqInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendRelaList;
    }

    private static FriendRelaInfo parseFriReq(JSONObject item) throws JSONException {
        FriendRelaInfo info = new FriendRelaInfo();
        info.setId(item.optInt("id"));
        info.setChatId(item.optLong("chatId"));

        info.setSenderId(item.optLong("senderId"));
        info.setSenderName(item.optString("senderName"));
        info.setSenderAvatar(item.optString("senderAvatar"));

        info.setReceiverId(item.optLong("receiverId"));
        info.setReceiverName(item.optString("receiverName"));
        info.setReceiverAvatar(item.optString("receiverAvatar"));

        info.setValidMsg(item.optString("validMsg"));
        info.setStatus(item.optInt("status"));
        info.setCreateAt(item.optLong("createAt"));
        info.setUpdateAt(item.optLong("updateAt"));

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

    public static FriendRelaInfo friRelaParser(JSONObject item) {
        FriendRelaInfo info = new FriendRelaInfo();
        info.setId(item.optInt("id"));

        info.setSenderId(item.optLong("senderId"));
        info.setSenderName(item.optString("senderName"));
        info.setSenderAvatar(item.optString("senderAvatar"));

        info.setReceiverId(item.optLong("receiverId"));
        info.setReceiverName(item.optString("receiverName"));
        info.setReceiverAvatar(item.optString("receiverAvatar"));

        info.setValidMsg(item.optString("validMsg"));
        info.setStatus(item.optInt("status"));
        info.setCreateAt(item.optLong("createAt"));
        info.setUpdateAt(item.optLong("updateAt"));
        return info;
    }

    public static EventType getJsonType(String message) {
        return EventType.fromString(message);
    }

    public static FriendInfo searchFriendParser(JSONObject json) {
        /**
         * {
         * 	"code": 0,
         * 	"data": {
         * 		"friend": true,
         * 		"friendInfoVO": {
         * 			"avatarUrl": "",
         * 			"updateAt": 0,
         * 			"username": "",
         * 			"uuNumber": 0
         *                },
         * 		"inBlackList": true    * 	},
         * 	"msg": ""
         * }
         */
        FriendInfo friendInfo = new FriendInfo();
        try {
            JSONObject data = json.getJSONObject("data");
            JSONObject friendVOJson = data.getJSONObject("friendInfoVO");
            String avatarUrl = friendVOJson.getString("avatarUrl");
            String username = friendVOJson.getString("username");
            long uuNumber = friendVOJson.getLong("uuNumber");
            long updateAt = friendVOJson.getLong("updateAt");
            boolean isFriend = data.getBoolean("friend");
            boolean inBlackList = data.getBoolean("inBlackList");

            friendInfo.setAvatarUrl(avatarUrl);
            friendInfo.setNickname(username);
            friendInfo.setUuNumber(uuNumber);
            friendInfo.setUpdateAt(updateAt);
            friendInfo.setFriend(isFriend);
            friendInfo.setBlack(inBlackList);
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
            Log.d(TAG, "[x] friendInfoListParser #218" + e.getMessage());
        }
        return friendList;
    }

    private static FriendInfo friendInfoParser(JSONObject item) throws JSONException {
        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setUuNumber(item.getLong("uuNumber"));
        friendInfo.setNickname(item.getString("username"));
        friendInfo.setAvatarUrl(item.getString("avatarUrl"));
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
        int msgId = item.getInt("msgId");

        long senderId = item.getLong("senderId");
        String senderName = item.getString("senderName");
        String senderAvatar = item.getString("senderAvatar");

        long chatId = item.getLong("chatId");
        int chatType = item.getInt("chatType");
        int messageType = item.getInt("messageType");
        String content = item.getString("content");
        long createAt = item.getLong("createAt");
        long updateAt = item.getLong("updateAt");

        msgInfo.setMsgId(msgId);
        msgInfo.setSenderId(senderId);
        msgInfo.setSenderName(senderName);
        msgInfo.setSenderAvatar(senderAvatar);
        msgInfo.setChatId(chatId);
        msgInfo.setChatType(chatType);
        msgInfo.setMessageType(messageType);
        msgInfo.setContent(content);
        msgInfo.setCreateAt(createAt);
        msgInfo.setUpdateAt(updateAt);

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

//        msgInfo.setMsgType(ContentType.TYPE_LINK);
//        msgInfo.setLinkTitle(linkTitle);
//        msgInfo.setLinkContent(linkContent);
//        msgInfo.setLinkImageUrl(linkImageUrl);
//        msgInfo.setSenderId(senderId);
//        msgInfo.setReceiverId(receiverId);

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
        roomInfo.setRoomId(Long.parseLong(roomId));
        roomInfo.setMovieUrl(movieUrl);
        return roomInfo;
    }

    public static RtcInfo rtcTokenParser(JSONObject json) throws JSONException {
        RtcInfo rtcInfo = new RtcInfo();
        String token = json.getString("token");
        String appId = json.getString("appId");
        long channelName = json.getLong("channelName");
        long userId = json.getLong("userId");
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
//            msgInfo.setLinkTitle(linkTitle);
//            msgInfo.setLinkContent(linkContent);
//            msgInfo.setLinkImageUrl(linkImageUrl);
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
        postInfo.setPoster(item.getLong("userId"));
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
        postInfo.setLiked(item.getBoolean("isLiked"));

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

    public static List<MovieCateInfo> movieCateParser(JSONArray data) {
        List<MovieCateInfo> movieCateInfoList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject item = data.getJSONObject(i);
                MovieCateInfo movieCateInfo = new MovieCateInfo();
                movieCateInfo.setCateId(item.getInt("cateId"));
                movieCateInfo.setCateName(item.getString("cateName"));
                movieCateInfoList.add(movieCateInfo);
                Log.d(TAG, "movieCateParser: " + movieCateInfo.toString());
            } catch (Exception e) {
                Log.d(TAG, "[x] movieCateParser #492" + e.getMessage());
            }
        }
        return movieCateInfoList;
    }

    public static List<PostInfo> likePostParser(JSONObject json) {
        List<PostInfo> postInfoList = new ArrayList<>();
        try {

            JSONArray postArray = json.getJSONArray("data");
            if (postArray.length() == 0) {
                Log.d(TAG, "[✓] likePostParser #503 数据为空");
                return postInfoList;
            }

            for (int i = 0; i < postArray.length(); i++) {
                try {
                    JSONObject postObj = postArray.getJSONObject(i);

                    PostInfo postInfo = new PostInfo();

                    // 解析基本字段
                    postInfo.setPostId(postObj.optInt("postId", 0));
                    postInfo.setPoster(postObj.optLong("userId", 0));
                    postInfo.setTitle(postObj.optString("title", ""));
                    postInfo.setContent(postObj.optString("content", ""));
                    postInfo.setLikeCount(postObj.optInt("likeCount", 0));
                    postInfo.setLiked(postObj.optBoolean("isLiked", false));
                    postInfo.setCommentCoverUrl(postObj.optString("coverUrl", ""));
                    // 解析图片URL数组
                    if (postObj.has("imageUrls") && !postObj.isNull("imageUrls")) {
                        JSONArray imageArray = postObj.getJSONArray("imageUrls");
                        List<String> imageUrls = new ArrayList<>();
                        for (int j = 0; j < imageArray.length(); j++) {
                            String imageUrl = imageArray.getString(j);
                            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                                // 如果需要处理URL，可以在这里添加
                                // imageUrl = UrlUtil.fillUrl(imageUrl);
                                imageUrls.add(imageUrl);
                            }
                        }
                        postInfo.setContentImagesUrlList(imageUrls);
                    }


                    if (postObj.has("tags")) {
                        // 解析标签数组（如果有）
                        JSONArray tagsArray = postObj.getJSONArray("tags");
                        List<TagInfo> tags = new ArrayList<>();
                        for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject tagObj = tagsArray.getJSONObject(j);
                            TagInfo tagInfo = new TagInfo();
                            tagInfo.setId(tagObj.optInt("id", 0));
                            tagInfo.setTagName(tagObj.optString("tagName", ""));
                            tags.add(tagInfo);
                        }

                    }

                    postInfoList.add(postInfo);

                } catch (JSONException e) {
                    Log.d(TAG, "[x] likePostParser #504 解析单个帖子失败, index=" + i + ", error: " + e.getMessage());
                    // 继续解析下一个，不中断整个解析过程
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "[x] likePostParser #506 " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "[x] likePostParser #507 未知错误: " + e.getMessage());
            e.printStackTrace();
        }

        return postInfoList;
    }

    public static PostDetailInfo postDetailParser(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");

            PostDetailInfo postDetailInfo = new PostDetailInfo();

            // 解析基本字段
            postDetailInfo.setPostId(data.getInt("postId"));
            postDetailInfo.setPostTitle(data.getString("postTitle"));
            postDetailInfo.setPostContent(data.getString("postContent"));
            postDetailInfo.setPosterId(data.getLong("posterId"));
            postDetailInfo.setLikeCount(data.getInt("likeCount"));
            postDetailInfo.setCommentCount(data.getInt("commentCount"));
            postDetailInfo.setLiked(data.getBoolean("liked"));

            // 解析图片URL列表
            JSONArray imagesArray = data.getJSONArray("contentImagesUrlList");
            List<String> imagesList = new ArrayList<>();
            for (int i = 0; i < imagesArray.length(); i++) {
                imagesList.add(imagesArray.getString(i));
            }
            postDetailInfo.setContentImagesUrlList(imagesList);

            // 解析标签数组
            JSONArray tagsArray = data.getJSONArray("tags");
            List<TagInfo> tags = new ArrayList<>();
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject tagObj = tagsArray.getJSONObject(i);
                TagInfo tag = new TagInfo();
                tag.setId(tagObj.getInt("id"));
                tag.setTagName(tagObj.getString("tagName"));
                tags.add(tag);
            }
            postDetailInfo.setTags(tags);

            return postDetailInfo;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] postDetailParser #555" + e.getMessage());
            return null;
        }
    }

    public static FriendInfo userByIdParser(JSONObject data) {
        try {
            FriendInfo friend = new FriendInfo();

            friend.setUuNumber(data.getLong("uuNumber"));
            friend.setNickname(data.getString("username"));
            friend.setAvatarUrl(data.getString("avatarUrl"));
            friend.setFollow(data.getBoolean("isFollow"));

            return friend;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] userByIdParser #578" + e.getMessage());
        }
        return null;
    }

    public static List<FriendInfo> userListByIdListParser(JSONObject json) {
        try {
            JSONArray dataArray = json.getJSONArray("data");
            List<FriendInfo> friendList = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                FriendInfo friend = userByIdParser(data);
                friendList.add(friend);
            }
            return friendList;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] userListByIdListParser #592" + e.getMessage());
        }
        return null;
    }

    public static MovieProfileInfo movieProfileParser(JSONObject json) {
        return null;
    }

    public static List<OnlineInfo> onlineListParser(JSONObject json) {
        try {
            JSONArray dataArray = json.getJSONArray("data");
            List<OnlineInfo> onlineList = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                OnlineInfo friend = onlineParser(data);
                onlineList.add(friend);
            }
            return onlineList;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] onlineListParser #699" + e.getMessage());
        }
        return null;
    }

    public static OnlineInfo onlineParser(JSONObject data) {
        try {
            OnlineInfo online = new OnlineInfo();

            online.setUserId(data.getLong("userId"));
            online.setOnline(data.getBoolean("isOnline"));

            return online;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] onlineParser #715" + e.getMessage());
        }
        return null;
    }

    public static UiChatInfo chatInfoByIdParser(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            UiChatInfo uiChatInfo = new UiChatInfo();
            uiChatInfo.setDisplayName(data.getString("displayName"));

            JSONArray memberIdArray = data.getJSONArray("memberIdList");
            List<Long> memberIdList = new ArrayList<>();
            for (int i = 0; i < memberIdArray.length(); i++) {
                memberIdList.add(memberIdArray.getLong(i));
            }
            uiChatInfo.setMemberIdList(memberIdList);

            return uiChatInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "[x] chatInfoByIdParser #738" + e.getMessage());
        }
        return null;
    }

    public static List<MsgListInfo> msgListParser(JSONObject json) throws JSONException {
        JSONArray data = json.getJSONArray("data");
        List<MsgListInfo> msgList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject msg = data.getJSONObject(i);
            MsgListInfo msgListInfo = new MsgListInfo();
            msgListInfo.setChatId(msg.getLong("chatId"));
            msgListInfo.setChatType(msg.getInt("chatType"));
            msgListInfo.setDisplayName(msg.getString("displayName"));
            msgListInfo.setAvatar(msg.getString("avatar"));
            msgList.add(msgListInfo);
        }
        return msgList;
    }

}
