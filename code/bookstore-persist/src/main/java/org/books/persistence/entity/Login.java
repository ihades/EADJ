package org.books.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Login extends IdentifiableObject {

    @Column(
            nullable = false,
            unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column
    private String groupname;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public Login() {
    }

    public Login(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordValid(String password) {
        return this.password != null && this.password.equals(password);
    }

}
