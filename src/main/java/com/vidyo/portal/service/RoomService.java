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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RoomService extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final ConcurrentHashMap<String, ReentrantLock> roomLocks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, RoomEntity> roomResponses = new ConcurrentHashMap<>();

    @Autowired
    private RoomConfig roomConfig;

    @Autowired
    private RoomRepository roomRepository;

    public RoomVO findOrCreateRoom(String roomName){
        RoomEntity room = getRoomByName(roomName);
        if(Objects.isNull(room)){
            // Getting lock particular room request
            ReentrantLock roomLock = getRoomLock(roomName);
            roomLock.lock();
            try{
                // Checking again after obtaining the lock
                room = roomResponses.get(roomName);
                if(Objects.isNull(room)){
                    // Creating room and saving it in the DB & internal storage
                    CreateScheduledRoomResponse scheduledRoomResponse = createScheduleRoom();
                    room = saveRoom(roomName, scheduledRoomResponse);
                    roomResponses.put(roomName,room);
                }
            }finally {
                synchronized (roomLock){
                    // Releasing the lock and notifying the waiting threads for same room request
                    roomLock.unlock();
                    roomLock.notifyAll();
                }
            }
        }

        return RoomEntityToVOConverter.convertEntityToVO(room);
    }

    public List<RoomVO> fetchAllRooms(){
        List<RoomEntity> roomsEntityList = roomRepository.findAllByDate(new Date());
        if(CollectionUtils.isEmpty(roomsEntityList)){
            return null;
        }
        return  RoomEntityToVOConverter.convertEntityListToVOList(roomsEntityList);
    }

    private RoomEntity getRoomByName(String roomName){
        List<RoomEntity> list = roomRepository.findByName(roomName.toLowerCase());
        return CollectionUtils.isEmpty(list)?null:list.get(0);
    }

    private CreateScheduledRoomResponse createScheduleRoom(){
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

    private RoomEntity saveRoom(String roomName, CreateScheduledRoomResponse scheduledRoomResponse){
        RoomEntity entity = new RoomEntity();
        entity.setName(roomName.toLowerCase());
        entity.setRoomURL(scheduledRoomResponse.getRoomURL());
        entity.setExtension(scheduledRoomResponse.getExtension());
        String[] url = scheduledRoomResponse.getRoomURL().split(AppConstants.REGEX);
        entity.setRoomKey(url[1]);

        return roomRepository.save(entity);
    }

    private ReentrantLock getRoomLock(String roomName){
        // Get or create a lock for the specified roomName
        ReentrantLock lock = roomLocks.computeIfAbsent(roomName, obj -> new ReentrantLock());

        // Wait for the lock to be available if it's already locked
        while(lock.isLocked() && !lock.isHeldByCurrentThread()){
            try{
                synchronized (lock) {
                    lock.wait();
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        return lock;
    }

}
