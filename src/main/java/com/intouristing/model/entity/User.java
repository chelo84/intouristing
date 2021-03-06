package com.intouristing.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] avatarImage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPosition userPosition;

    @ManyToMany
    private List<ChatGroup> chatGroups;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "destination")
    private List<Request> requestsAsDestination;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private List<Request> requestsAsSender;

}
