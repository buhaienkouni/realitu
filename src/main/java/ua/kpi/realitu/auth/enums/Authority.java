package ua.kpi.realitu.auth.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    WRITE_ARTICLE,
    WRITE_MY_ARTICLE,
    WRITE_USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
