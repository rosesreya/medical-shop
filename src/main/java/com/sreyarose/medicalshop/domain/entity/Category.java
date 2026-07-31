package com.sreyarose.medicalshop.domain.entity;

public class Category {
    private Long id;
    private String name;

    public Category(Long id,String name){
        this.id = id;
        setName(name);
    }
    public Category(String name){
        setName(name);
    }
    public void setName(String name){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public Long getId(){
        return id;
    }
}
