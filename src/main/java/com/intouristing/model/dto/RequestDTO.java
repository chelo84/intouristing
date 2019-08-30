package com.intouristing.model.dto;

import com.intouristing.model.entity.Request;
import com.intouristing.model.entity.User;
import com.intouristing.model.enumeration.RelationshipTypeEnum;
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

    private Long senderId;

    private Long destinationId;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime declinedAt;

    public static RequestDTO parseDTO(Request request) {
        if (nonNull(request)) {
            UserDTO senderDTO = UserDTO.parseDTO(request.getSender()),
                    destinationDTO = UserDTO.parseDTO(request.getDestination());
            return RequestDTO
                    .builder()
                    .id(request.getId())
                    .sender(senderDTO)
                    .destination(destinationDTO)
                    .senderId(Optional.ofNullable(senderDTO).map(UserDTO::getId).orElse(null))
                    .destinationId(Optional.ofNullable(destinationDTO).map(UserDTO::getId).orElse(null))
                    .type(request.getType().name())
                    .createdAt(request.getCreatedAt())
                    .acceptedAt(request.getAcceptedAt())
                    .declinedAt(request.getDeclinedAt())
                    .build();
        }

        return null;
    }

    public static RequestDTO parseDTO(User sender, User destination, RelationshipTypeEnum relationshipType) {
        if (nonNull(sender) && nonNull(destination) && nonNull(relationshipType)) {
            UserDTO senderDTO = UserDTO.parseDTO(sender),
                    destinationDTO = UserDTO.parseDTO(destination);
            return RequestDTO
                    .builder()
                    .sender(senderDTO)
                    .destination(destinationDTO)
                    .senderId(Optional.ofNullable(senderDTO).map(UserDTO::getId).orElse(null))
                    .destinationId(Optional.ofNullable(destinationDTO).map(UserDTO::getId).orElse(null))
                    .type(relationshipType.name())
                    .build();
        }

        return null;
    }
}
