package com.memory.wq.utils;

import android.util.Log;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.beans.UserInfo;
import com.memory.wq.enumertions.JsonType;
import com.memory.wq.enumertions.SearchUserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateJson {

    private static final String TAG = "WQ_GenerateJson";

    public static String generateJson(JsonType type, String email, int code, String password) {
        String json = "";
        switch (type) {
            case JSONTYPE_REQUEST:
                json = "{\"email\":\"" + email + "\"}";
                break;
            case JSONTYPE_REGISTER:
                json = "{\"email\":\"" + email + "\",\"code\":\"" + code + "\",\"password\":\"" + password + "\"}";
                break;
            case JSONTYPE_LOGIN:
                json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
                break;

        }
        return json;
    }

    public static String getApplyFriendJson(String targetEmail) {
        JSONObject object = new JSONObject();
        try {
            object.put("targetEmail", targetEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getUpdateRelaJson(String sourceEmail, boolean isAgree) {
        JSONObject object = new JSONObject();
        try {
            object.put("agree", isAgree);
            object.put("requestEmail", sourceEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getSearchUserJson(SearchUserType type, String targetAccount) {
        JSONObject object = new JSONObject();
        try {
            switch (type) {
                case SEARCH_USER_TYPE_EMAIL:
                    object.put("email", targetAccount);
                    object.put("phone", "");
                    object.put("uuNumber", "");
                    break;
                case SEARCH_USER_TYPE_UUNUM:
                    object.put("email", "");
                    object.put("phone", "");
                    object.put("uuNumber", targetAccount);
                    break;
                case SEARCH_USER_TYPE_PHONE:

                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getMsgJson(String targetEmail, String content) {
        JSONObject object = new JSONObject();
        try {
            object.put("msg", content);
            object.put("targetEmail", targetEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getSaveRoomJson(String roomId, int movieId) {
        JSONObject object = new JSONObject();
        try {
            object.put("roomId", roomId);
            object.put("movieId", movieId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getReleaseRoomJson(String roomId) {
        JSONObject object = new JSONObject();
        try {
            object.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getRtcToken(String roomId, int role, String userId) {

        JSONObject object = new JSONObject();
        try {
            object.put("channelName", roomId);
            object.put("expire", 3600);
            object.put("role", role);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }


    public static String getUpdateUserInfoJson(UserInfo userInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("userName", userInfo.getUserName());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getShareMsgJson(MsgInfo shareMsg) {
        JSONObject object = new JSONObject();
        try {
            object.put("targetEmail", shareMsg.getReceiverEmail());
            object.put("linkTitle", shareMsg.getLinkTitle());
            object.put("linkContent", shareMsg.getLinkContent());
            object.put("linkImageUrl", shareMsg.getLinkImageUrl());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getPostContentJson(PostInfo postInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("title", postInfo.getTitle());
            object.put("content", postInfo.getContent());
            if (postInfo.getContentImagesUrlList() == null) {
                object.put("images", new JSONArray());
                return object.toString();
            }

            JSONArray imagesArray = new JSONArray();
            for (String url : postInfo.getContentImagesUrlList()) {
                imagesArray.put(url);
            }

            object.put("images", imagesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getQueryPostJson(QueryPostInfo queryPostInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("page", queryPostInfo.getPage());
            object.put("size", queryPostInfo.getSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getCommentJson(int postId, QueryPostInfo queryPostInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("page", queryPostInfo.getPage());
            object.put("postId", postId);
            object.put("size", queryPostInfo.getSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getAddCommentJson(PostCommentInfo postCommentInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("content", postCommentInfo.getContent());
            object.put("postId", postCommentInfo.getPostId());
            object.put("replyToUserId", postCommentInfo.getReplyToUserId());
            object.put("parentId", postCommentInfo.getParentId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getMyPostJson(QueryPostInfo queryPostInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("page", queryPostInfo.getPage());
            object.put("size", queryPostInfo.getSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getLoadMsgJson(String targetEmail) {
        JSONObject object = new JSONObject();
        try {
            object.put("chatId", targetEmail);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getLoadMsgJson #233"+e.getMessage());
        }
        return object.toString();
    }
}
