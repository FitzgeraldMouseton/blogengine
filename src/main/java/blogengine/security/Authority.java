package blogengine.security;

public enum Authority {

    POST_READ("post:read"),
    POST_WRITE("post:write"),
    POST_MODERATE("post:moderate"),
    VOTE_ADD("vote:add");

    private final String authority;

    Authority(String authority) {
        this.authority = authority;
    }
}
