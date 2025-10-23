package com.project.ecommerce.exceptionhandler;

public class ResourceNotFoundException extends RuntimeException
{
    String resourceName;
    String fieldName;
    long fieldId;

    public ResourceNotFoundException(String resource, String field, long fieldId)
    {
        super(String.format("%s not found with %s : %s", resource, field, fieldId));
        this.resourceName = resource;
        this.fieldName = field;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException(String fieldName, String resource)
    {
        super(String.format("%s not found with %s", fieldName, resource));
        this.fieldName = fieldName;
        this.resourceName = resource;
    }

}
