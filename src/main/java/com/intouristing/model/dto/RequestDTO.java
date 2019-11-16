package com.intouristing.model.dto;

import com.intouristing.model.entity.Request;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    private Long id;

    private UserDTO sender;

    private UserDTO destination;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime declinedAt;

    public static RequestDTO parseDTO(Request request) {
        if (nonNull(request)) {
            RequestDTO requestDTO = parseDTO(
                    request.getSender(),
                    request.getDestination(),
                    request.getType()
            );
            assert nonNull(requestDTO);
            requestDTO.setId(request.getId());
            requestDTO.setCreatedAt(request.getCreatedAt());
            requestDTO.setAcceptedAt(request.getAcceptedAt());
            requestDTO.setDeclinedAt(request.getDeclinedAt());
            return requestDTO;
        }

        return null;
    }

    public static RequestDTO parseDTO(User sender, User destination, RelationshipType relationshipType) {
        if (nonNull(sender) && nonNull(destination)) {
            return RequestDTO
                    .builder()
                    .sender(
                            UserDTO.parseDTO(sender, destination.getUserPosition())
                    )
                    .destination(
                            UserDTO.parseDTO(destination, sender.getUserPosition())
                    )
                    .type(relationshipType.name())
                    .build();
        }

        return null;
    }

}
