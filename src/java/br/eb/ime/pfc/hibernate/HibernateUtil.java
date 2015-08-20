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
package br.eb.ime.pfc.hibernate;

import br.eb.ime.pfc.domain.AccessLevel;
import br.eb.ime.pfc.domain.Feature;
import br.eb.ime.pfc.domain.Layer;
import br.eb.ime.pfc.domain.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        SessionFactory sf = null;
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            final String hibernateConfigurationFilePath = "/br/eb/ime/pfc/hibernate/hibernate.cfg.xml";
            sf = new AnnotationConfiguration().configure(hibernateConfigurationFilePath).buildSessionFactory();
        } catch (Throwable ex) {
        }
        finally{
            sessionFactory = sf;
        }
    }
    
    public static SessionFactory getSessionFactory() throws HibernateException{
        if(sessionFactory != null){
            return sessionFactory;
        }
        else{
            throw new HibernateException("Could not initialize Hibernate.");
        }
    }
    
    public static Session getCurrentSession() throws HibernateException{
        if(sessionFactory != null){
            //Throws HibernateException if there is no Session open.
            return sessionFactory.getCurrentSession();
        }
        else{
            throw new HibernateException("Could not initialize Hibernate");
        }
    }
    
    public static void main(String args[]){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        AccessLevel ac1 = new AccessLevel("operacional");
        User user1 = new User("user2","12345",ac1);
        Layer lay1 = new Layer("Restaurantes","Restaurantes");
        Feature feat1 = new Feature("endereco","end",lay1);
        ac1.addLayer(lay1);
        lay1.addFeature(feat1);
        //session.save(feat1);
        session.save(lay1);
        session.save(ac1);
        session.save(user1);
        session.getTransaction().commit();
        session.close();
    }
}
