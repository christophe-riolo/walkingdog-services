package com.hubesco.software.walkingdog.authentication.api;

/**
 *
 * @author paoesco
 */
public class SignupData {

    private String email;
    private String password;
    private String dogName;
    private DogGender dogGender;
    private DogBreed dogBreed;
    private String dogBirthdate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public DogGender getDogGender() {
        return dogGender;
    }

    public void setDogGender(DogGender dogGender) {
        this.dogGender = dogGender;
    }

    public DogBreed getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(DogBreed dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getDogBirthdate() {
        return dogBirthdate;
    }

    public void setDogBirthdate(String dogBirthdate) {
        this.dogBirthdate = dogBirthdate;
    }

    @Override
    public String toString() {
        return "SignupData{" + "email=" + email + ", dogName=" + dogName + ", dogGender=" + dogGender + ", dogBreed=" + dogBreed + ", dogBirthdate=" + dogBirthdate + '}';
    }

}
