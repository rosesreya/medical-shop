package com.sreyarose.medicalshop.presentation.dto;

public class CategoryResponse {
    private String name;
    private Long id;
    public CategoryResponse(Long id, String name){
        this.id = id;
        this.name = name;
    }
    public Long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
}
