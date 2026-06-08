package com.example.publisher;

import javax.xml.ws.Endpoint;
import com.example.soap.SOAPIimpl;

public class ServicePublisher {
    public static void main(String[] args) {
        String url = "http://localhost:1516/WS/Users";
        
        Endpoint.publish(url, new SOAPIimpl());
        
        System.out.println("Servicio SOAP publicado en: " + url + "?wsdl");
        System.out.println("Servidor corriendo. Presiona Enter para detener.");
        
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
