package com.main.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;

    private String name;

    private String email;

    private String passwordHash;

    private Date createdAt;

    private Date updatedAt;

    // OneToMany
    // OnDeleteAction = OnDeleteAction.Cascade
    private List<Investiment> investiments;

    // OneToOne
    // OnDeleteAction = OnDeleteAction.Cascade
    private Wallet wallet;
}
