package sequence.sequence_member.member.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MemberPrincipal implements OAuth2User, UserDetails {

    private final MemberEntity member;
    private final Map<String, Object> attributes;

    public MemberPrincipal(MemberEntity member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public MemberEntity getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("MEMBER"));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    public static MemberPrincipal create(MemberEntity member, Map<String, Object> attributes) {
        return new MemberPrincipal(member, attributes);
    }
}