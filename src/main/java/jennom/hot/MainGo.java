package jennom.hot;

import com.google.gson.Gson;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import jennom.jms.MessageSenderObj;
import jennom.jms.MessageSenderTxt;
import jennom.jms.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@DependsOn(value = {"txtSender", "objSender", "universalMessageListener"})
public class MainGo extends javax.swing.JFrame {
    
    private User user;
    @Inject
    private MessageSenderTxt txtSender;
    @Inject
    private MessageSenderObj objSender;
    @Inject
    private Gson gson;      

    @PostConstruct
    public void afterBirn() {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
        // JMS-test
        for(int i=1; i < 10; ++i) {
            user=new User();
            user.setLogin("tt"+i+i);
            user.setPassw("tt"+i+i);
            txtSender.sendMessageQ("harp07qq", gson.toJson(user)); 
            txtSender.sendMessageT("harp07tt", gson.toJson(user));
        } 
        for(int i=1; i < 10; ++i) {
            user=new User();
            user.setLogin("oo"+i+i);
            user.setPassw("oo"+i+i);
            objSender.sendMessage("harp07qq", user); 
        }           
    }
    
    @PreDestroy
    public void beforeKill() {
       System.out.println("stop application..........."); 
    }    

    public synchronized static void main(String args[]) {
        //System.out.println(AppContext.class.getClassLoader().toString());
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
        ctx.registerShutdownHook();
        //System.out.println("Enter 'stop' to close");
        Scanner sc = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                if (sc.next().toLowerCase().trim().equals("stop")) {
                    System.out.println("Closing");
                    System.exit(0);
                    break;
                }
            }
        }).start();
        System.out.println("Enter 'stop' to close");
    }
}
