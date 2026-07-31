package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    protected CategoryJpaEntity(){

    }
    public CategoryJpaEntity(Long id, String name){
        this.id = id;
        this.name = name;
    }
   /*  public void setId(Long id){
        this.id = id;
    }*/
    public Long getId(){
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }


}
