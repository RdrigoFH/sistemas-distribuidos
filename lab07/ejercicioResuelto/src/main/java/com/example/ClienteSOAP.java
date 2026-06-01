package com.example;

import java.net.URL;
 
import javax.xml.namespace.QName;
import javax.xml.ws.Service; 

public class ClienteSOAP { 
    public static void main(String[] args) throws Exception { 
        URL url = new URL("http://localhost:8080/calculadora?wsdl"); 
        
        QName qname = new QName("http://example.com/", "CalculadoraSOAPService");        
        Service service = Service.create(url, qname); 
        CalculadoraSOAPPort calc = service.getPort(CalculadoraSOAPPort.class); 
        
        System.out.println("Resultado de la suma: " + calc.sumar(10, 20)); 
    } 
}
