package blogengine.security;

import java.util.Set;
import com.google.common.collect.Sets;

public enum Role {
    USER(Sets.newHashSet(Authority.POST_WRITE, Authority.VOTE_ADD)),
    MODERATOR(Sets.newHashSet(Authority.POST_WRITE, Authority.POST_MODERATE, Authority.VOTE_ADD));

    private final Set<Authority> authorities;

    Role(Set<Authority> authorities) {
        this.authorities = authorities;
    }
}
