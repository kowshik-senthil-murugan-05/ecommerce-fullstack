package com.project.ecommerce.exceptionhandler;

public class APIResponse<T>
{
    public String message;
    public boolean status;
    public T savedObject;

    public APIResponse(String msg, boolean status, T obj)
    {
        this.message = msg;
        this.status = status;
        this.savedObject = obj;
    }
}
