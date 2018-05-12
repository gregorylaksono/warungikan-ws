package org.warungikan.api.config;

public class SecurityConstants {

    public static final String SECRET = "w4rung1k4n_6r3g_oSbxNOGBs9_X0X0";
    public static final long EXPIRATION_TIME = 172800000; // 2 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/register";
    public static final String VERIFY_USER_URL = "/user/verify";
    public static final String ADMIN_DOMAIN = "/admin/**";
}
