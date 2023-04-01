/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author Danylo
 */
@JMSDestinationDefinition(name = "java:app/jms/NewsQueue", 
        interfaceName = "jakarta.jms.Queue", 
        resourceAdapter = "jmsra", 
        destinationName = "NewsQueue")
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/NewsQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NewsMDB implements MessageListener {
    
    @PersistenceContext
    private EntityManager em;
    
    public NewsMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        ObjectMessage msg = null;
        TextMessage tmsg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                NewsItem e = (NewsItem) msg.getObject();
                em.persist(e);
            }
            else if(message instanceof TextMessage){
                tmsg = (TextMessage) message;
                NewsItem item = new NewsItem();
                String[] parts = tmsg.getText().split("\\|",2);
                item.setHeading(parts[0]);
                item.setBody(parts[1]);
                em.persist(item);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}