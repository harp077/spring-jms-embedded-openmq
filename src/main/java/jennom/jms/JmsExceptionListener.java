package jennom.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

//import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JmsExceptionListener implements ExceptionListener {

	private static final Logger LOG = Logger.getLogger(JmsExceptionListener.class.getName());

	@Override
	public void onException(JMSException e) {
		String errorMessage = "Exception while processing the JMS message";
		LOG.log(Level.WARNING, errorMessage);		
	}

}
