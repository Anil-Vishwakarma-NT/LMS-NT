package com.nt.LMS.service;

import com.nt.LMS.dto.LoginDto;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.TokenResponseDto;

public interface AuthService {
    public TokenResponseDto login(LoginDto loginDto);
    public TokenResponseDto refreshToken(String refreshToken);
    public MessageOutDto logout(String email);

}
