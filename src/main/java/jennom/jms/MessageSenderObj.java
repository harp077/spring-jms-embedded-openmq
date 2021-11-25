package jennom.jms;

import com.google.gson.Gson;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("objSender")
@DependsOn(value = {"universalMessageListener"})
public class MessageSenderObj implements BeanNameAware { //implements MessageSender {
    
    private String myName;
    @Inject
    private Gson gson;
    @Inject
    private JmsTemplate jmsQueueTemplate;
    
    @Override
    public void setBeanName(String bname) {
       this.myName=bname; 
       //System.out.println("my name is = " + this.myName);
    }    
    
    @PostConstruct
    public void afterBirn() {
        System.out.println("my name is = " + this.myName);
    }    

    //@Override
    //@Async
    public void sendMessage(String destinationNameQ, User user) {
        //jmsTemplate.setDeliveryDelay(555L);
        this.jmsQueueTemplate.convertAndSend(destinationNameQ, user);
        //loggerBean.info(" >>> Sending obj user = " + gson.toJson(user));
        System.out.println(destinationNameQ + " <<< Sending obj user GSON = " + gson.toJson(user));
        //System.out.println(" >>> Sending obj user thread = " + Thread.currentThread().getName()+", run at: " + ISDTF.stf.format(new Date()));
    }

}
