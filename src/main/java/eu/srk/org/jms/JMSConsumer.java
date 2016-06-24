package eu.srk.org.jms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import eu.srk.org.Queue;
import eu.srk.org.XMLInterface;

public class JMSConsumer implements MessageListener {

	final static Logger logger = Logger.getLogger(JMSConsumer.class);

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	private String queue;
	private String brokerUrl;
	private String userName;
	private String password;
	private Queue qu;

	public JMSConsumer(Properties props, Queue qu1) throws JMSException {

		brokerUrl = props.getProperty("jms_brokerurl");
		queue = props.getProperty("jms_consumer_queue");
		userName = props.getProperty("jms_username");
		password = props.getProperty("jms_password");
		qu = qu1;

		factory = new ActiveMQConnectionFactory(userName, password, brokerUrl);

		connection = factory.createConnection();
		connection.setClientID("rw_sender");

		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queue);
		consumer = session.createConsumer(destination);
		consumer.setMessageListener(this);
		// Now that 'receive' setup is complete, start the Connection
		connection.start();

	}

	public static void main(String args[]) {
		JMSConsumer consumer;
		Queue qu = null;
		try {
			Properties prop = new Properties();
			InputStream inStream = new FileInputStream("src/main/resources/jms.properties");
			prop.load(inStream);
			consumer = new JMSConsumer(prop, qu);
		} catch (JMSException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);

		} finally {
			System.exit(1);
		}

	}

	public void onMessage(javax.jms.Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage text = (TextMessage) message;

				XMLInterface translator = new XMLInterface();
				String ascii = translator.xmlToString(text.getText());
				qu.putQueue(ascii);
				// System.out.println("Message is : " + text.getText());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
