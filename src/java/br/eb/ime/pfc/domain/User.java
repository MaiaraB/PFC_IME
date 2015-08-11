package br.eb.ime.pfc.domain;

import java.util.Objects;

/**
 * A representation of a User in this system.
 * 
 * A user contains an username and a password and it is uniquely identified
 * by its username.
 * A user also contains an access level that allows him to access a limited 
 * group of layers.
 */
public class User {
    private final String username;
    private final String password;
    private final AccessLevel accessLevel;
    
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
            return (other.username.equals(this.username));
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