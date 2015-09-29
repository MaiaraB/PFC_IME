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
import br.eb.ime.pfc.domain.Style;
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
            throw new HibernateException("No session open.");
        }
    }
    
    private HibernateUtil(){
        
    }
    
    public static void main(String args[]){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        //AccessLevel
        AccessLevel operacional =  AccessLevel.makeAccessLevel("operacional");
        AccessLevel estrategico = AccessLevel.makeAccessLevel("estrategico");
        AccessLevel tatico = AccessLevel.makeAccessLevel("tatico");
        //User
        User userTatico = User.makeUser("falcon","123",tatico);
        User userOperacional = User.makeUser("wolf","123",operacional);
        User userEstrategico = User.makeUser("lion","123",estrategico);
        //Layer
        Layer bairros =  Layer.makeLayer("Bairros","rio2016:bairro_part");
        bairros.setOpacity(0.5);
        Layer locaisDeInteresse =  Layer.makeLayer("Locais de Interesse","rio2016:locais_de_interesse");
        Layer atracoes =  Layer.makeLayer("Atrações","rio2016:atracoes");
        Layer atracoesComite =  Layer.makeLayer("Atrações do Comitê","rio2016:atracoes_comite");
        Layer competicoes =  Layer.makeLayer("Competições","rio2016:competicoes");
        Layer hoteis =  Layer.makeLayer("Hotéis","rio2016:hoteis");
        hoteis.setStyle(new Style("pinpoint"));
        Layer lanchesRefeicoes =  Layer.makeLayer("Lanches e Refeições","rio2016:lanches_refeicoes");
        Layer corpoBombeiros =  Layer.makeLayer("Corpo de Bombeiros","rio2016:corpo_de_bombeiros");
        Layer delegacias =  Layer.makeLayer("Delegacias Policiais","rio2016:delegacias_policiais");
        Layer metro =  Layer.makeLayer("Paradas de Metro","rio2016:paradas_metro");
        Layer onibus =  Layer.makeLayer("Paradas de Ônibus","rio2016:paradas_onibus");
        Layer trem =  Layer.makeLayer("Paradas de Trem","rio2016:paradas_trens");
        
        //Tatico
        tatico.addLayer(bairros);
        tatico.addLayer(trem);
        tatico.addLayer(corpoBombeiros);
        tatico.addLayer(delegacias);
        tatico.addLayer(metro);
        tatico.addLayer(onibus);
        //Operacional
        operacional.addLayer(bairros);
        operacional.addLayer(trem);
        operacional.addLayer(locaisDeInteresse);
        operacional.addLayer(atracoes);
        operacional.addLayer(atracoesComite);
        operacional.addLayer(competicoes);
        operacional.addLayer(hoteis);
        operacional.addLayer(lanchesRefeicoes);
        //Estrategico
        estrategico.addLayer(bairros);
        estrategico.addLayer(locaisDeInteresse);
        estrategico.addLayer(atracoes);
        estrategico.addLayer(atracoesComite);
        estrategico.addLayer(competicoes);
        estrategico.addLayer(hoteis);
        estrategico.addLayer(lanchesRefeicoes);
        estrategico.addLayer(corpoBombeiros);
        estrategico.addLayer(delegacias);
        estrategico.addLayer(metro);
        estrategico.addLayer(onibus);
        estrategico.addLayer(trem);
        //ADD FEATURES TO LAYERS
        bairros.addFeature(new Feature("Bairro","nm_bairro"));
        hoteis.addFeature(new Feature("Nome","nome"));
        hoteis.addFeature(new Feature("Endereço","endereco"));
        hoteis.addFeature(new Feature("Bairro","bairro"));
        hoteis.addFeature(new Feature("Telefone","telefone"));
        hoteis.addFeature(new Feature("Fax","fax"));
        hoteis.addFeature(new Feature("Categoria","categoria"));
        hoteis.addFeature(new Feature("Email","email"));
        hoteis.addFeature(new Feature("Idiomas","idiomas"));
        //SAVE LAYERS
        
        session.save(bairros);
        session.save(locaisDeInteresse);
        session.save(atracoes);
        session.save(atracoesComite);
        session.save(competicoes);
        session.save(hoteis);
        session.save(lanchesRefeicoes);
        session.save(corpoBombeiros);
        session.save(delegacias);
        session.save(metro);
        session.save(onibus);
        session.save(trem);
        
        //SAVE ACCESS LEVEL
        session.save(operacional);
        session.save(estrategico);
        session.save(tatico);
        //SAVE USERS
        session.save(userOperacional);
        session.save(userEstrategico);
        session.save(userTatico);
        
        session.getTransaction().commit();
        session.close();
    }
}
