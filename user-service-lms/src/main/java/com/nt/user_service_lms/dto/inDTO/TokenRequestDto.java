package com.nt.user_service_lms.dto.inDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequestDto {
    /**
     * Refresh Token cannot be null.
     */
    private String refreshToken;


    /**
     * Checks if the object is equal.
     *
     * @param o
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TokenRequestDto that = (TokenRequestDto) o;
        return Objects.equals(refreshToken, that.refreshToken);
    }

    /**
     * generates hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(refreshToken);
    }
}
