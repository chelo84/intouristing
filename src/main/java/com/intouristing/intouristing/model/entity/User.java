package com.intouristing.intouristing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Data
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserSearchControl userSearchControl;

}
