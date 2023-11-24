package com.vidyo.portal.repository;

import com.vidyo.portal.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
        List<RoomEntity> findByName(String name);

        List<RoomEntity> findAllByDate(Date currentDate);
}
