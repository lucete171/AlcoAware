package com.example.alcoaware_00;

public class UserAccount {
    public UserAccount(){}
    public UserAccount(String name, int age, String region, String gender, String drinkFrequency, String drinkLocation) {
        this.name = name;
        this.age = age;
        this.region = region;
        this.gender = gender;
        this.drinkFrequency = drinkFrequency;
        this.drinkLocation = drinkLocation;
    }
    // ----------------------------회원가입----------------------------
    public String getIdToken() {return idToken;}
    public void setIdToken(String idToken){
        this.idToken = idToken;
    }
    public String idToken;

    public String getEmailId() {return emailId;}
    public void setEmailId(String idToken){
        this.emailId = emailId;
    }
    public String emailId;

    public String getPassword() {return password;}
    public void setPassword(String password) {
        this.password = password;
    }
    public String password;

    // ----------------------------사용자 상세 정보----------------------------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private String name;
    //--------------------------------------------------------
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    private int age;
    //--------------------------------------------------------
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    private String region;
    //--------------------------------------------------------
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    private String gender;
    //--------------------------------------------------------
    public String getDrinkFrequency() {
        return drinkFrequency;
    }
    public void setDrinkFrequency(String drinkFrequency) {
        this.drinkFrequency = drinkFrequency;
    }
    private String drinkFrequency;
    //--------------------------------------------------------
    public String getDrinkLocation() {
        return drinkLocation;
    }
    public void setDrinkLocation(String drinkLocation) {
        this.drinkLocation = drinkLocation;
    }
    private String drinkLocation;
}