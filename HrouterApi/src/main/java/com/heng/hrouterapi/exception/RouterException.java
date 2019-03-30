package com.heng.hrouterapi.exception;

public class RouterException extends RuntimeException {
    private int type;

    public RouterException( int type,String message) {
        super(message);
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
