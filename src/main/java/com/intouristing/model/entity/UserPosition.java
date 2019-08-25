package com.intouristing.model.entity;

import com.intouristing.model.dto.UserPositionDTO;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Setter
@Getter
@EqualsAndHashCode(exclude = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserPosition {

    @Id
    private Long id;

    private Double latitude;

    private Double longitude;

    private Double accuracy;

    private Double speed;

    private Double heading;

    @OneToOne
    @MapsId
    private User user;

    public static UserPosition parseUserPosition(UserPositionDTO userPositionDTO) {
        if (nonNull(userPositionDTO)) {
            return UserPosition
                    .builder()
                    .latitude(userPositionDTO.getLatitude())
                    .longitude(userPositionDTO.getLongitude())
                    .accuracy(userPositionDTO.getAccuracy())
                    .speed(userPositionDTO.getSpeed())
                    .heading(userPositionDTO.getHeading())
                    .build();
        }
        return new UserPosition();
    }
}
