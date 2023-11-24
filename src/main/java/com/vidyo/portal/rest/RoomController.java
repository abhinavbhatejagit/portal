package com.vidyo.portal.rest;

import com.vidyo.portal.model.RoomVO;
import com.vidyo.portal.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin()
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/getRoom")
    public RoomVO findOrCreateRoom(@RequestBody RoomVO roomVO){
         return roomService.findOrCreateRoom(roomVO.getRoomName());
    }

    @GetMapping("/fetchAllRooms")
    public List<RoomVO> fetchAllRooms(){
        return roomService.fetchAllRooms();
    }

}
