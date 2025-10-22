package com.example.server.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Info
    private String firstname;
    private String lastname;
    private String middlename;
    private String suffix;

    // Address Info
    @Column(name = "blk_room")
    private String blkRoom;

    private String building;
    private String street;
    private String barangay;
    private String province;

    @Column(name = "zip_code")
    private String zipCode;

    // Contact Info
    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "tel_no")
    private String telNo;

    private String email;

    // ID Info
    @Column(name = "valid_id_type")
    private String validIdType;

    @Column(name = "valid_id_number")
    private String validIdNumber;

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getBlkRoom() {
        return blkRoom;
    }

    public void setBlkRoom(String blkRoom) {
        this.blkRoom = blkRoom;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getValidIdType() {
        return validIdType;
    }

    public void setValidIdType(String validIdType) {
        this.validIdType = validIdType;
    }

    public String getValidIdNumber() {
        return validIdNumber;
    }

    public void setValidIdNumber(String validIdNumber) {
        this.validIdNumber = validIdNumber;
    }
}
