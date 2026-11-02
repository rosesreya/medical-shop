package com.sreyarose.medicalshop.presentation.dto;

public class CreateManufacturerRequest {
     private String name;

    public CreateManufacturerRequest(){

    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
