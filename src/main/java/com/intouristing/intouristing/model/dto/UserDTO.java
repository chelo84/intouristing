package com.intouristing.intouristing.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intouristing.intouristing.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private UserPositionDTO userPosition;

    private String token;

    private String email;

    public static UserDTO parseDTO(User user) {
        if (nonNull(user)) {
            return UserDTO
                    .builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .userPosition(UserPositionDTO.parseDTO(user.getUserPosition()))
                    .token(user.getToken())
                    .email(user.getEmail())
                    .build();
        }
        return null;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

}