package com.sreyarose.medicalshop.presentation.dto;

public class CreateCategoryRequest {
    private String name;

    public CreateCategoryRequest(){

    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

}
