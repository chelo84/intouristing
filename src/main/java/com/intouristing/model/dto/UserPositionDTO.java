package com.intouristing.model.dto;

import com.intouristing.model.entity.User;
import com.intouristing.model.entity.UserPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPositionDTO {

    private Long id;

    private Double latitude;

    private Double longitude;

    private Double accuracy;

    private Double speed;

    private Double heading;

    private Long user;

    public static UserPositionDTO parseDTO(UserPosition userPosition) {
        if (nonNull(userPosition)) {
            return UserPositionDTO
                    .builder()
                    .id(userPosition.getId())
                    .latitude(userPosition.getLatitude())
                    .longitude(userPosition.getLongitude())
                    .accuracy(userPosition.getAccuracy())
                    .speed(userPosition.getSpeed())
                    .heading(userPosition.getHeading())
                    .user(Optional.ofNullable(userPosition.getUser()).map(User::getId).orElse(null))
                    .build();
        }
        return null;
    }

}
