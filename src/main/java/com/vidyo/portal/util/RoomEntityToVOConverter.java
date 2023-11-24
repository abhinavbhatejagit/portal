package com.vidyo.portal.util;

import com.vidyo.portal.entity.RoomEntity;
import com.vidyo.portal.model.RoomVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class RoomEntityToVOConverter {

    public static RoomVO convertEntityToVO(RoomEntity entity){
        return new RoomVO(entity.getName(), entity.getRoomURL(), entity.getRoomKey(), entity.getExtension());
    }

    public static List<RoomVO> convertEntityListToVOList(List<RoomEntity> entityList){
        return entityList.stream().map(e -> convertEntityToVO(e)).collect(Collectors.toList());
    }
}
