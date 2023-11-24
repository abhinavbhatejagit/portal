package com.vidyo.portal.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class RoomVO implements Serializable {


    private String roomName;

    private String roomURL;

    private String roomKey;

    private String extension;

}
