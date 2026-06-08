package com.example.client;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.example.soap.SOAPI;
import com.example.model.User;

public class UserClient {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:1516/WS/Users?wsdl");
            QName qname = new QName("http://soap.example.com/", "SOAPIimplService");            
            Service service = Service.create(url, qname);
            SOAPI soapService = service.getPort(SOAPI.class);
            
            System.out.println("=== LISTA DE USUARIOS INICIAL ===");
            for(User user : soapService.getUsers()) {
                System.out.println(user);
            }
            
            System.out.println("\n=== AGREGANDO NUEVO USUARIO ===");
            soapService.addUser(new User("Pablo", "Ruiz"));
            
            System.out.println("\n=== LISTA DE USUARIOS ACTUALIZADA ===");
            for(User user : soapService.getUsers()) {
                System.out.println(user);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
