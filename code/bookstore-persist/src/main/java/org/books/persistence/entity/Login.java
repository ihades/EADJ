package org.books.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(name = "Login.findByUserName", query = "SELECT l "
            + "FROM Login l "
            + "WHERE l.userName = :userName")
})
@Entity
public class Login extends IdentifiableObject {

    public transient static final String LOGIN_FIND_BY_USER_NAME = "userName";

    @Column(nullable = false,
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
