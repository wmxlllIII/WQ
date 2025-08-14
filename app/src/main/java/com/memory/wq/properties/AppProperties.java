package com.memory.wq.properties;

public class AppProperties {
    public static final String MY_DB_NAME = "Memory.db";

    public static final String SP_NAME = "users";
    public static final String USER_TABLE_NAME = "users";
    public static final String FRIEND_TABLE_NAME = "friends";
    public static final String MESSAGE_TABLE_NAME = "message";
    public static final String CREATE_TABLE_FRIEND_RELATIONSHIP = "friend_relationship";
    public static final String AUTHORITY = "com.memory.wq.albumprovider";
    public static final String AVATAR_RECEIVER_ACTION = "setCurrentAvatar";


    public static final String HTTP_SERVER_ADDRESS = "http://47.105.108.147:8080";
    //明文传输xml里也有地址!!!
    public static final String WEB_SOCKET_SERVER_ADDRESS = "ws://47.105.108.147:8080/ws/";
    public static final String FRIEND_RELATIONSHIP = HTTP_SERVER_ADDRESS + "/auth/friend/allrequests";
    public static final String UPLOAD_URL = HTTP_SERVER_ADDRESS + "/auth/avatar";
    public static final String SEARCH_USER = HTTP_SERVER_ADDRESS + "/auth/searchUser";
    public static final String FRIEND_REQ = HTTP_SERVER_ADDRESS + "/auth/friend/apply";
    public static final String FRIEND_RES = HTTP_SERVER_ADDRESS + "/auth/friend/applyResult";
    public static final String ALL_FRIENDS = HTTP_SERVER_ADDRESS + "/auth/friend/getAllFriends";

    public static final String REQUEST_URL = HTTP_SERVER_ADDRESS + "/auth/sendcode";
    public static final String REGISTER_URL = HTTP_SERVER_ADDRESS + "/auth/register";
    public static final String LOGIN_URL = HTTP_SERVER_ADDRESS + "/auth/login";
    public static final String SEND_MSG = HTTP_SERVER_ADDRESS + "/auth/msg/send";
    public static final String UPDATE_USER = HTTP_SERVER_ADDRESS + "/auth/updateuserinfo";

    public static final String MOVIES = HTTP_SERVER_ADDRESS + "/auth/movie/movies";
    public static final String ROOMS = HTTP_SERVER_ADDRESS + "/auth/movie/rooms";
    public static final String SAVE_ROOM = HTTP_SERVER_ADDRESS + "/auth/movie/saveroom";
    public static final String REMOVE_ROOM = HTTP_SERVER_ADDRESS + "/auth/movie/removeroom";
    public static final String SHARE_ROOM = HTTP_SERVER_ADDRESS + "/auth/message/shareroom";
    public static final String POST_PUBLISH = HTTP_SERVER_ADDRESS + "/auth/post";
    public static final String POST_GET = HTTP_SERVER_ADDRESS + "/auth/getpost";
    public static final String COMMENT_GET = HTTP_SERVER_ADDRESS + "/auth/getComment";
    public static final String COMMENT_ADD = HTTP_SERVER_ADDRESS + "/auth/addComment";

    public static final String AGORA_TOKEN = HTTP_SERVER_ADDRESS + "/api/token";


    public static final String ROLE_TYPE = "ROLE_TYPE";
    public static final String MOVIE_PATH = "MOVIE_PATH";
    public static final String ROOM_ID = "ROOM_ID";

    public static final String FRIENDINFO = "FRIENDINFO";
    public static final String SHARE_MESSAGE = "SHARE_MESSAGE";
    public static final String POSTINFO = "postInfo";

}
