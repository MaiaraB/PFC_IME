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
package br.eb.ime.pfc.domain;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author arthurfernandes
 */
public class AccessLevelManager {
    private final Session session;
    
    public AccessLevelManager(Session session){
        this.session = session;
        assert this.session!=null;
    }
    
    public void create(AccessLevel accessLevel) throws HibernateException{
        this.session.save(accessLevel);
    }
    
    public AccessLevel getAccessLevel(String name) throws HibernateException{
        return (AccessLevel) this.session.get(AccessLevel.class, name);
    }
    
    public void update(AccessLevel accessLevel) throws HibernateException{
        this.session.update(accessLevel);
    }
    
    public void delete(AccessLevel accessLevel) throws HibernateException{
        this.session.delete(accessLevel);
    }
    
}