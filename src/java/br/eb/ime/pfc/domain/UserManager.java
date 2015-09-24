package br.eb.ime.pfc.domain;

import java.util.Comparator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/*
 * The MIT License
 *
 * Copyright 2015 arthurfernandes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 *
 */
public class UserManager {
    private final Session session;
    public UserManager(Session session){
        this.session = session;
    }
    
    public void create(User user) throws HibernateException{
        if(this.session.get(User.class,user.getUsername())!=null){
            throw new ObjectDuplicateException("There's an user with the specified username");
        }
        this.session.merge(user);
    }
    
    public User getById(String username) throws HibernateException{
        final User user =  (User) this.session.get(User.class, username);
        if(user == null){
            throw new ObjectNotFoundException("No such User with the specified username");
        }
        return user;
    }
    
    public void update(User user) throws HibernateException{
        this.getById(user.getUsername());
        this.session.merge(user);
    }
    
    public void delete(String username) throws HibernateException{
        final User user = (User) this.session.get(User.class, username);
        if(user == null){
            throw new ObjectNotFoundException("No such User with the specified username");
        }
        this.session.delete(user);
    }
    
    public List<User> readAll() throws HibernateException{
        final Query query = session.createQuery("from User");
        final List<User> allUsers = query.list();
        allUsers.sort(new Comparator<User>(){
            @Override
            public int compare(User o1, User o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });
        for(User user : allUsers){
            Hibernate.initialize(user);
        }
        return allUsers;
    }
}
