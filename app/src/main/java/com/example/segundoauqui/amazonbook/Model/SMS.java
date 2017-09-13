package com.example.segundoauqui.amazonbook.Model;

/**
 * Created by auquisegundo on 20/08/2017.
 */

public class SMS {
    String number, message;

    public SMS(String number, String message) {
        this.number = number;
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
