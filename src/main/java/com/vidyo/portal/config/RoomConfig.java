package com.vidyo.portal.config;

import com.vidyo.portal.service.RoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


@Configuration
public class RoomConfig {

    @Value("${portal.default-uri}")
    private String defaultUri;

    @Value("${portal.user.name}")
    private String userName;

    @Value("${portal.user.password}")
    private String userPassword;

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.vidyo.portal.wsdl");
        return marshaller;
    }

    @Bean
    public RoomService roomClient(Jaxb2Marshaller marshaller) {
        RoomService client = new RoomService();
        client.setDefaultUri(defaultUri);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


}
