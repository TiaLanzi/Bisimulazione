package com.example.bisimulazione.models;

public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String mail;
    private String password;

    // constructor without arguments set all to "unknown"
    public User() {
        super();
        // setFirstName(R.string.user_unknown);
        String unknown = "sconosciuto";
        this.setFirstName(unknown);
        this.setLastName(unknown);
        this.setUsername(unknown);
        this.setMail(unknown);
        this.setPassword(unknown);
    }

    public User(String firstName, String lastName, String username, String mail, String password) {
        setFirstName(firstName);
        setLastName(lastName);
        setUsername(username);
        setMail(mail);
        setPassword(password);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}