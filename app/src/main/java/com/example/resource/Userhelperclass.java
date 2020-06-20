package com.example.resource;


import android.widget.EditText;

public class Userhelperclass {
    private String name, phone;

    public Userhelperclass() {

    }

    public Userhelperclass(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }
}
