package com.wanted.naeil.global.auth.service;

import com.wanted.naeil.global.auth.model.dto.AuthDetails;
import com.wanted.naeil.domain.user.dto.response.LoginUserDTO;
import com.wanted.naeil.domain.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUserDTO login = memberService.findByUsername(username);

        if (Objects.isNull(login)) {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다.");
        }

        return new AuthDetails(login);
    }



}