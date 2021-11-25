package jennom.jms;

import com.google.gson.Gson;
import com.sun.messaging.jmq.jmsclient.ObjectMessageImpl;
import com.sun.messaging.jmq.jmsclient.TextMessageImpl;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;
import org.springframework.jms.annotation.JmsListener;

@Component
public class UniversalMessageListener implements BeanNameAware {

    private String myName;
    @Inject
    private Gson gson;  
    
    @Override
    public void setBeanName(String bname) {
       this.myName=bname; 
       //System.out.println("my name is = " + this.myName);
    }    
    
    @PostConstruct
    public void afterBirn() {
        System.out.println("my name is = " + this.myName);
    }      

    // ONLY 1-listener for 1-queue !!!
    //@Async
    @JmsListener(destination = "harp07qq", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(Object message) {
        try {
        //System.out.println(" >>> Listener INFO: Received object/text (?) = " + message.getClass());
        //System.out.println(" >>> Listener INFO: Received thread = " + Thread.currentThread().getName()+", run at: " + ISDTF.stf.format(new Date()));
        //if (message.getClass().getName().equals("jennom.jms.User")) {
        //if (message instanceof User) { 
        if (message.getClass() == ObjectMessageImpl.class) {
            try {
                ObjectMessageImpl activeMQObjectMessage = (ObjectMessageImpl) message;
                User user = (User) activeMQObjectMessage.getObject();
                //ObjectMessage receivedMessage=(ObjectMessage) jmsTemplate.receive();
                //User user = (User) receivedMessage.getObject();
                //JOptionPane.showMessageDialog(null, " >>> Listener INFO: Received object user GSON = " + gson.toJson(user), "info", JOptionPane.ERROR_MESSAGE); 
                System.out.println(" >>> Listener INFO: Received object user GSON = " + gson.toJson(user));
                //System.out.println(" >>> Listener INFO: Received object user GSON = " + message.getClass().getName());
            } catch (JMSException | NullPointerException je) {
                System.out.println("Listener WARNING: JMS error = " + je.getMessage());                
            }
        } 
        //
        if (message.getClass() == TextMessageImpl.class) {
            TextMessage textMessage = (TextMessage) message;
            try {
                User user = gson.fromJson(textMessage.getText(), User.class );
                //System.out.println("objListener INFO: >>> Received: " + textMessage.getText());
                //JOptionPane.showMessageDialog(null, " >>> Listener INFO: Received object user GSON = " + gson.toJson(user), "info", JOptionPane.ERROR_MESSAGE); 
                System.out.println(" >>> Listener INFO: Received text user GSON = " + gson.toJson(user));
                //System.out.println(" >>> Listener INFO: Received object user GSON = " + message.getClass().getName());
            } catch (JMSException ex) {
                System.out.println("Listener WARNING: JMS error = " + ex.getMessage());
            }                 
        }
        } catch (Exception ee) {
            System.out.println("Listener Exception error = " + ee.getMessage());
        }
    }
    
    //@Async
    @JmsListener(destination = "harp07tt")
    public void receiveTopic(String message) {
        System.out.println("Отслеживать тему ============= Отслеживать тему");
        System.out.println(message);
 
    }    

}
