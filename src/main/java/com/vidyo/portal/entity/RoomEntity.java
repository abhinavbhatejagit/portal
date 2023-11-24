package com.vidyo.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String roomURL;

    private String roomKey;

    private String extension;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @PrePersist
    private void onCreate() {
        date = new Date();
    }

}
