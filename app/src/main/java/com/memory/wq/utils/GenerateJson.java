package com.memory.wq.utils;

import android.util.Log;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.beans.QueryPostInfo;
import com.memory.wq.beans.PostInfo;
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.enumertions.JsonType;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.enumertions.UpdateInfoType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class GenerateJson {

    private static final String TAG = "WQ_GenerateJson";

    public static String getLoginJson(String authValue, String password, int authType) {
        JSONObject object = new JSONObject();
        try {
            object.put("authValue", authValue);
            object.put("authType", authType);
            object.put("password", password);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getLoginJson #61" + e.getMessage());
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getValidCodeJson(String authValue) {
        JSONObject object = new JSONObject();
        try {
            object.put("email", authValue);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getValidCodeJson #72" + e.getMessage());
            e.printStackTrace();
        }
        return object.toString();
    }


    public static String getRegisterJson(String authValue, String password, int code) {
        JSONObject object = new JSONObject();
        try {
            object.put("email", authValue);
            object.put("password", password);
            object.put("code", code);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getRegisterJson #61" + e.getMessage());
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getApplyFriendJson(long targetId, String validMsg) {
        JSONObject object = new JSONObject();
        try {
            object.put("targetId", targetId);
            object.put("validMsg", validMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getUpdateRelaJson(long sourceUuNumber, boolean isAgree, String validMsg) {
        JSONObject object = new JSONObject();
        try {
            object.put("isAgree", isAgree);
            object.put("validMsg", validMsg);
            object.put("sourceUuNumber", sourceUuNumber);
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

    public static String getSendMsgJson(long chatId, ChatType chatType, String msg, ContentType msgType) {
        JSONObject object = new JSONObject();
        try {
            object.put("chatId", chatId);
            object.put("chatType", chatType.toInt());
            object.put("msgType", msgType.toInt());
            object.put("msg", msg);
        } catch (JSONException e) {
            Log.d(TAG, "[x] getMsgJson #90");
        }
        return object.toString();
    }

    public static String getSaveRoomJson(long roomId, int movieId) {
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

    public static String getRtcToken(long roomId, int role, long userId) {

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


    public static String getUpdateUserInfoJson(UpdateInfoType updateType, Object data) {
        JSONObject object = new JSONObject();
        try {
            object.put("type", updateType);
            switch (updateType) {
                case USERNAME:
                case EMAIL:
                case SIGNATURE:
                    object.put("data", data.toString());
                    break;
                case GENDER:
                    object.put("data", (boolean) data);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, "[x] getUpdateUserInfoJson #155 " + e.getMessage());
        }
        return object.toString();
    }

    public static String getShareMsgJson(MsgInfo shareMsg) {
        JSONObject object = new JSONObject();
//        try {
//            object.put("targetId", shareMsg.getReceiverId());
//            object.put("linkTitle", shareMsg.getLinkTitle());
//            object.put("linkContent", shareMsg.getLinkContent());
//            object.put("linkImageUrl", shareMsg.getLinkImageUrl());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return object.toString();
    }

    public static String getPostContentJson(PostInfo postInfo) {
        JSONObject object = new JSONObject();
        try {
            object.put("title", postInfo.getTitle());
            object.put("content", postInfo.getContent());
//            if (postInfo.getContentImagesUrlList() == null) {
//                object.put("images", new JSONArray());
//                return object.toString();
//            }
//
//            JSONArray imagesArray = new JSONArray();
//            for (String url : postInfo.getContentImagesUrlList()) {
//                imagesArray.put(url);
//            }
//
//            object.put("images", imagesArray);
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

    public static String getLoadMsgJson(String targetEmail, int page, int size) {
        JSONObject object = new JSONObject();
        try {
            object.put("chatId", targetEmail);
            object.put("page", page);
            object.put("size", size);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getLoadMsgJson #233" + e.getMessage());
        }
        return object.toString();
    }

    public static String getFollowJson(long userId) {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getFollowJson #245" + e.getMessage());
        }
        return object.toString();
    }

    public static String getDeleteFriendJson(long friendId) {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", friendId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getDeleteFriendJson #245" + e.getMessage());
        }
        return object.toString();
    }

    public static String getLikeCommentJson(int postId) {
        JSONObject object = new JSONObject();
        try {
            object.put("postId", postId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getLikeCommentJson #245" + e.getMessage());
        }
        return object.toString();
    }

    public static String getSaveProgressJson(int movieId, int currentProgress) {
        JSONObject object = new JSONObject();
        try {
            object.put("movieId", movieId);
            object.put("progress", currentProgress);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getSaveProgressJson #287" + e.getMessage());
        }
        return object.toString();
    }

    public static String getMoviesByCateJson(int cateId) {
        JSONObject object = new JSONObject();
        try {
            object.put("cateId", cateId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getMoviesByCateJson #297" + e.getMessage());
        }
        return object.toString();
    }

    public static String getMoviesByActorJson(int actorId) {
        JSONObject object = new JSONObject();
        try {
            object.put("actorId", actorId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getMoviesByActor #305" + e.getMessage());
        }
        return object.toString();
    }

    public static String getActorInfoJson(int actorId) {
        JSONObject object = new JSONObject();
        try {
            object.put("actorId", actorId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getActorInfoJson #315" + e.getMessage());
        }
        return object.toString();
    }

    public static String getSaveFootprintJson(int postId) {
        JSONObject object = new JSONObject();
        try {
            object.put("postId", postId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getSaveFootprintJson #344" + e.getMessage());
        }
        return object.toString();
    }

    public static String getPostDetailJson(int postId) {
        JSONObject object = new JSONObject();
        try {
            object.put("postId", postId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getPostDetailJson #354" + e.getMessage());
        }
        return object.toString();
    }

    public static String getUserByIdJson(long userId) {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getUserByIdJson #364" + e.getMessage());
        }
        return object.toString();
    }

    public static String getMovieDetailJson(int movieId) {
        JSONObject object = new JSONObject();
        try {
            object.put("movieId", movieId);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getMovieDetailJson #374" + e.getMessage());
        }
        return object.toString();
    }

    public static String getBuildGroupJson(String groupName, String groupAvatar, Set<Long> selectedUsers) {
        JSONObject object = new JSONObject();
        try {
            JSONArray memberIds = new JSONArray();
            for (Long userId : selectedUsers) {
                memberIds.put(userId);
            }
            object.put("memberIds", memberIds);
            object.put("groupName", groupName);
            object.put("groupAvatar", groupAvatar);
        } catch (JSONException e) {
            Log.d(TAG, "[X] getBuildGroupJson #394" + e.getMessage());
        }
        return object.toString();
    }
}
