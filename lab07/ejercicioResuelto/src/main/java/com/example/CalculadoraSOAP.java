
package com.example;

import javax.jws.WebService;

@WebService
public class CalculadoraSOAP implements CalculadoraSOAPPort {
    @Override
    public int sumar(int a, int b){ 
        return a + b; 
    } 
}