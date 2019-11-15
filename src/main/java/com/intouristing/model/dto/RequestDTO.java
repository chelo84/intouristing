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

    private Long senderId;

    private String senderName;

    private String senderLastName;

    private Long destinationId;

    private String destinationName;

    private String destinationLastName;

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
                    .senderId(Optional.ofNullable(sender.getId())
                            .orElse(null)
                    )
                    .senderName(Optional.ofNullable(sender.getName())
                            .orElse(null)
                    )
                    .senderLastName(Optional.ofNullable(sender.getLastName())
                            .orElse(null)
                    )
                    .destinationId(Optional.ofNullable(destination.getId())
                            .orElse(null)
                    )
                    .destinationName(Optional.ofNullable(destination.getName())
                            .orElse(null)
                    )
                    .destinationLastName(Optional.ofNullable(destination.getLastName())
                            .orElse(null)
                    )
                    .type(relationshipType.name())
                    .build();
        }

        return null;
    }

}
