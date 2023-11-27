package ua.kpi.realitu.auth.enums;

import java.util.Set;

public enum Role {
    SUPER_ADMIN,
    COPYWRITER;

    public Set<Authority> getAuthorities() {
        if (this == SUPER_ADMIN) {
            return Set.of(Authority.WRITE_ARTICLE, Authority.WRITE_USER);
        } else if (this == COPYWRITER) {
            return Set.of(Authority.WRITE_MY_ARTICLE);
        }
        return Set.of();
    }
}
