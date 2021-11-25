
package jennom.jms;

import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

public class EmbeddedBrokerEventListener implements BrokerEventListener {

    public void brokerEvent(BrokerEvent brokerEvent) {
        System.out.println("Received broker event:" + brokerEvent);
    }

    public boolean exitRequested(BrokerEvent event, Throwable thr) {
        System.out.println("Broker is about to shut down because of:" + event + " with " + thr);
        // return value will be ignored
        return true;
    }
    
}
