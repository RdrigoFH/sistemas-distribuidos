package com.example;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(name = "CalculadoraSOAP")
public interface CalculadoraSOAPPort {
    @WebMethod
    int sumar(int a, int b);
}