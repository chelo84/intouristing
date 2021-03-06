package com.intouristing.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.UserPosition;
import com.intouristing.utils.PositionUtils;
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

    private String name;

    private String lastName;

    private String username;

    private String password;

    private UserPositionDTO userPosition;

    private String email;

    private Long distance;

    public static UserDTO parseDTO(User user) {
        if (nonNull(user)) {
            return UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .userPosition(
                            UserPositionDTO.parseDTO(
                                    user.getUserPosition()
                            )
                    )
                    .email(user.getEmail())
                    .build();
        }
        return null;
    }

    public static UserDTO parseDTO(User user, UserPosition currUserPosition) {
        var userDTO = parseDTO(user);
        if (nonNull(userDTO)) {
            var distance = PositionUtils.calculateDistance(
                    user.getUserPosition(),
                    currUserPosition
            );
            userDTO.setDistance(distance);
        }
        return userDTO;
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
