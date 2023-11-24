package com.vidyo.portal.service;

import com.vidyo.portal.entity.RoomEntity;
import com.vidyo.portal.model.RoomVO;
import com.vidyo.portal.repository.RoomRepository;
import com.vidyo.portal.util.AppConstants;
import com.vidyo.portal.util.RoomEntityToVOConverter;
import com.vidyo.portal.wsdl.*;
import com.vidyo.portal.config.RoomConfig;
import com.vidyo.portal.util.BasicAuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.util.*;

public class RoomService extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    private RoomConfig roomConfig;

    @Autowired
    private RoomRepository roomRepository;

    public CreateScheduledRoomResponse createScheduleRoom(){
        CreateScheduledRoomRequest req = new CreateScheduledRoomRequest();

        CreateScheduledRoomResponse response = (CreateScheduledRoomResponse) getWebServiceTemplate()
                .marshalSendAndReceive(req,
                        new WebServiceMessageCallback() {

                            public void doWithMessage(WebServiceMessage message) {
                                TransportContext context = TransportContextHolder.getTransportContext();
                                HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
                                connection.getConnection().addRequestProperty(AppConstants.AUTHORIZATION,
                                        BasicAuthenticationUtil.generateBasicAuthenticationHeader(roomConfig.getUserName(),
                                                roomConfig.getUserPassword()));
                            }});

        return response;
    }

    public RoomVO findOrCreateRoom(String roomName){
        RoomEntity room = getRoomByName(roomName);
        if(Objects.isNull(room)){
            CreateScheduledRoomResponse scheduledRoomResponse = createScheduleRoom();
            room = saveRoom(roomName, scheduledRoomResponse);
        }

        return RoomEntityToVOConverter.convertEntityToVO(room);
    }

    public RoomEntity getRoomByName(String roomName){
        List<RoomEntity> list = roomRepository.findByName(roomName.toLowerCase());
        return CollectionUtils.isEmpty(list)?null:list.get(0);
    }

    public RoomEntity saveRoom(String roomName, CreateScheduledRoomResponse scheduledRoomResponse){
        RoomEntity entity = new RoomEntity();
        entity.setName(roomName.toLowerCase());
        entity.setRoomURL(scheduledRoomResponse.getRoomURL());
        entity.setExtension(scheduledRoomResponse.getExtension());
        String[] url = scheduledRoomResponse.getRoomURL().split(AppConstants.REGEX);
        entity.setRoomKey(url[1]);

        return roomRepository.save(entity);
    }

    public List<RoomVO> fetchAllRooms(){
        List<RoomEntity> roomsEntityList = roomRepository.findAllByDate(new Date());
        if(CollectionUtils.isEmpty(roomsEntityList)){
            return null;
        }
        return  RoomEntityToVOConverter.convertEntityListToVOList(roomsEntityList);
    }
}
