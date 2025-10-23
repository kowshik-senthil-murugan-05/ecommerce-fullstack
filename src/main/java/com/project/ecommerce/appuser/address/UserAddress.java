package com.project.ecommerce.appuser.address;

import com.project.ecommerce.util.AppUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = UserAddress.USER_ADDRESS_TABLE_NAME)
public class UserAddress
{
    public static final String USER_ADDRESS_TABLE_NAME = AppUtil.TABLE_NAME_PREFIX + "user_address";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String buildingNo;
    @NotBlank
    private String buildingName;
    @NotBlank
    private String streetNo;
    @NotBlank
    private String streetName;
    @NotBlank
    private String city;
    @NotBlank
    private long pincode;

    @Column(nullable = false)
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAddressId() {
        return id;
    }

    public void setAddressId(long addressId) {
        this.id = addressId;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public long getPincode() {
        return pincode;
    }
}
