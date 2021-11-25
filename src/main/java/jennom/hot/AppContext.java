package jennom.hot;

import com.google.gson.Gson;
import com.sun.messaging.ConnectionConfiguration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//
import org.springframework.jms.annotation.EnableJms;
import javax.jms.ConnectionFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import jennom.jms.JmsExceptionListener;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;
import com.sun.messaging.jmq.jmsserver.Globals;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;
import java.util.Properties;
import javax.jms.JMSException;
import jennom.jms.EmbeddedBrokerEventListener;

@Configuration
@ComponentScan(basePackages = {"jennom"})
@EnableAsync
@EnableJms
public class AppContext {

    private String concurrency = "1-8";
    //private String brokerURL = "tcp://localhost:7676";
    /*private static final String IMQ_HOME = "";//AppContext.class.getClassLoader().getResource("../openmq").getPath();
    private static final String IMQ_VAR_HOME = IMQ_HOME.concat("/var");
    private static final String IMQ_LIB_HOME = IMQ_HOME.concat("/lib");
    private static final String IMQ_INSTANCE_HOME = IMQ_VAR_HOME.concat("/instances");
    private static final String IMQ_INSTANCE_NAME = "imqbroker";*/

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    //=========== new 21-11-2021
    
    /*private static String[] getArguments() {
        return new String[]{
            "-varhome", IMQ_VAR_HOME,
            "-libhome", IMQ_LIB_HOME,
            "-imqhome", IMQ_HOME
        };
    }

    private static void updateConfigurationForBroker() {
        System.out.println(Globals.getConfig().toString());
        try {
            Globals.getConfig().updateProperty("imq.home", IMQ_HOME);
            Globals.getConfig().updateProperty("imq.libhome", IMQ_LIB_HOME);
            Globals.getConfig().updateProperty("imq.varhome", IMQ_VAR_HOME);
            Globals.getConfig().updateProperty("imq.instanceshome", IMQ_INSTANCE_HOME);
            Globals.getConfig().updateProperty("imq.instancename", IMQ_INSTANCE_NAME);
            Globals.getConfig().updateBooleanProperty("imq.persist.file.newTxnLog.enabled", false, true);
            Globals.getConfig().updateBooleanProperty("imq.cluster.enabled", false, true);
//          Globals.getConfig().updateProperty("imq.autocreate.destination.maxNumMsgs", "100");
        } catch (Exception e) {
            System.out.println("Unable to set the configuration for the broker" + e.getMessage());
            //EmbeddedBroker.log.warn("Unable to set the configuration for the broker", e);
        }
    }*/

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(JmsExceptionListener jmsExceptionListener) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory(jmsExceptionListener));
        factory.setDestinationResolver(destinationResolver());
        factory.setConcurrency(concurrency);
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory(JmsExceptionListener jmsExceptionListener) {
        return createJmsConnectionFactory(jmsExceptionListener);
    }

    private ConnectionFactory createJmsConnectionFactory(JmsExceptionListener jmsExceptionListener) {
        /*Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        env.put(Context.PROVIDER_URL, "file:///j/webapp/openmq-5.1.2/data");
        com.sun.messaging.ConnectionFactory bcf=null;
        try {
            Context ctx = new InitialContext(env);
            bcf = (com.sun.messaging.ConnectionFactory) ctx.lookup("ConnectionFactory");
        } catch (NamingException ex) {
            Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //ConnectionFactory bcf = new ConnectionFactory(brokerURL);
            //bcf.setExceptionListener(jmsExceptionListener);
            bcf.setProperty(ConnectionConfiguration.imqDefaultPassword, "guest");
            bcf.setProperty(ConnectionConfiguration.imqDefaultUsername, "guest");
        } catch (JMSException ex) {
            Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        ClientRuntime clientRuntime = ClientRuntime.getRuntime();
        BrokerInstance brokerInstance = null;
        try {
            brokerInstance = clientRuntime.createBrokerInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        BrokerEventListener listener = new EmbeddedBrokerEventListener();
        Properties props = new Properties();// = brokerInstance.parseArgs(args);
        //props.setProperty("imqhome", "broker-home");
        //props.setProperty("varhome", "broker-home/var");
        // Initialize the broker instance using the specified properties and broker event listener
        //updateConfigurationForBroker();
        brokerInstance.init(props, listener);
        // now start the embedded broker
        brokerInstance.start();
        com.sun.messaging.ConnectionFactory bcf = new com.sun.messaging.ConnectionFactory();
        try {
            bcf.setProperty(ConnectionConfiguration.imqAddressList, "mq://localhost/direct");
        } catch (JMSException ex) {
            Logger.getLogger(AppContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*EmbeddedBroker broker = EmbeddedBroker.builder().homeTemp().build();
        broker.run();
        ConnectionFactory bcf = broker.connectionFactory();*/
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(bcf);
        connectionFactory.setExceptionListener(jmsExceptionListener);
        return connectionFactory;
    }

    @Bean(name = "jmsQueueTemplate")
    public JmsTemplate createJmsQueueTemplate(ConnectionFactory jmsConnectionFactory) {
        return new JmsTemplate(jmsConnectionFactory);
    }

    @Bean(name = "jmsTopicTemplate")
    public JmsTemplate createJmsTopicTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
        // TOPIC NOT LISTEN WITH template.setPubSubDomain(true); !!!!!!!!!!!!!!!!!!
        //template.setPubSubDomain(true);
        return template;
    }

    @Bean
    public DestinationResolver destinationResolver() {
        return new DynamicDestinationResolver();
    }

    /// ========================= JMS ============================

    /*@Bean
    public Queue queue(){
        return new ActiveMQQueue("harp07qq");
    }
    
    @Bean
    public Topic topic(){
        return new ActiveMQTopic("harp07tt");
    }  
    
    @Bean 
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory amqCF=new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
        //amqCF.setPassword("admin");
        //amqCF.setUserName("admin");
	return amqCF;
    }

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory() {
	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	factory.setConnectionFactory(connectionFactory());
	factory.setConcurrency("3-5");
	return factory;
    }

    @Bean 
    public JmsTemplate jmsTemplate() {
	JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        // Default Destination
	jmsTemplate.setDefaultDestination(queue());
        // then use jmsTemplate.setDeliveryDelay(5000L); in ActiveMQ -> ERROR !!!!!!!
        //jmsTemplate.setDeliveryDelay(5000L);
        //jmsTemplate.setPubSubDomain(true);
	return jmsTemplate;
    }  */
    //========================================
    @Bean(name = "gson")
    public Gson gson() {
        return new Gson();
    }

}
