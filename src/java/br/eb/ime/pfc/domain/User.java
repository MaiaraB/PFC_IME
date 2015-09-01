package br.eb.ime.pfc.domain;

import java.io.Serializable;
import java.util.Objects;
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
public class User implements Serializable,Cloneable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "USER_ID") private final String username;
    
    @Column(name = "PASSWORD") private final String password;
    
    @ManyToOne(optional=false)
    @JoinColumn(name = "ACCESSLEVEL_ID",referencedColumnName="ACCESSLEVEL_ID")
    private final AccessLevel accessLevel;
    
    /*
    * Representation Invariant:
    * username must not be empty.
    * password must not be empty.
    * accessLevel must not be null;
    */
    
    /**
     * Constructs a user with the specified username, password and Access Level
     * @param username
     *      The name that identifies this user. This name must not be empty.
     * @param password
     *      The password used by this user to identify himself in the system.
     * @param accessLevel 
     *      The access level used by this user.
     */
    public User(String username,String password,AccessLevel accessLevel){
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
        checkRep();
    }
    
    protected User(){
        this.username = null;
        this.password = null;
        this.accessLevel = null;
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
    
    @Override
    public User clone(){
        return new User(this.username,
                        this.password,
                        this.accessLevel.clone());
    }
}