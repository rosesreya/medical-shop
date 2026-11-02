package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private boolean active;

    protected CategoryJpaEntity(){

    }
    public CategoryJpaEntity(Long id, String name, boolean active){
        this.id = id;
        this.name = name;
        this.active = active;
    }
    public Long getId(){
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public boolean isActive(){
        return active;
    }
}
