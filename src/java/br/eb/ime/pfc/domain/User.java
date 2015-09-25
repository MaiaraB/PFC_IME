package br.eb.ime.pfc.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A representation of a User in this system.
 * 
 * A user contains an username and a password and it is uniquely identified
 * by its username.
 * A user also contains an access level that allows him to access a limited 
 * group of layers.
 */
@Entity
@Table(name = "users")
public class User implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "USER_ID") private final String username;
    
    @Column(name = "PASSWORD") private final String password;
    
    private String name;
    private String email;
    private String telephone;
    
    @ManyToOne(optional=false,cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ACCESSLEVEL_ID",referencedColumnName="ACCESSLEVEL_ID")
    private AccessLevel accessLevel;
    
    /*
    * Representation Invariant:
    * username must not be empty.
    * password must not be empty.
    * accessLevel must not be null;
    */
    
    public static User makeUser(String username,String password,AccessLevel accessLevel){
        if(isValid(username)){
            return new User(username,password,accessLevel);
        }
        else{
            throw new ObjectInvalidIdException("Could not create User because the specified username is invalid");
        }
    }
    
    public static boolean isValid(String username){
        if(username.equals("")){
            return false;
        }
        if(username.contains(" ") || username.contains("\n")){
            return false;
        }
        else{
            return true;
        }
    }
    
    /**
     * Constructs a user with the specified username, password and Access Level
     * @param username
     *      The name that identifies this user. This name must not be empty.
     * @param password
     *      The password used by this user to identify himself in the system.
     * @param accessLevel 
     *      The access level used by this user.
     */
    protected User(String username,String password,AccessLevel accessLevel){
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
        this.name = "";
        this.telephone = "";
        this.email = "";
        checkRep();
    }
    
    protected User(){
        this.username = null;
        this.password = null;
        this.accessLevel = null;
        this.name = "";
        this.telephone = "";
        this.email = "";
    }
    
    /**
     * Checks the representation invariant of this object.
     */
    private void checkRep(){
        assert !this.username.equals("");
        assert !this.password.equals("");
        assert accessLevel != null;
    }
    
    /**
     * Returns the username of this user.
     * @return username
     */
    public String getUsername(){
        return this.username;
    }
    
    /**
     * Returns the password of this user.
     * @return password
     */
    public String getPassword(){
        return this.password;
    }
    
    /**
     * Returns the access level of this user.
     * @return access level
     */
    public AccessLevel getAccessLevel(){
        return this.accessLevel;
    }
    
    public void setAccessLevel(AccessLevel accessLevel){
        this.accessLevel = accessLevel;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getTelephone(){
        return this.telephone;
    }
    
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof User){
            final User other = (User) o;
            return (other.getUsername().equals(this.username));
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username);
    }

}