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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class JMSSender {

	final static Logger logger = Logger.getLogger(JMSSender.class);

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageProducer producer = null;

	private String queue;
	private String brokerUrl;
	private String userName;
	private String password;

	public JMSSender(Properties props) throws JMSException {
		
		System.out.println("Hallo");

		brokerUrl = props.getProperty("jms_brokerurl");
		queue = props.getProperty("jms_sender_queue");
		userName = props.getProperty("jms_username");
		password = props.getProperty("jms_password");

		factory = new ActiveMQConnectionFactory(userName, password, brokerUrl);

		connection = factory.createConnection();
		connection.setClientID("rw_sender");

		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue(queue);
		producer = session.createProducer(destination);

	}

	public void sendMessage(String msg) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(msg);
		producer.send(message);
	}

	public static void main(String args[]) {
		JMSSender sender;
		try {
			Properties prop = new Properties();
			InputStream inStream = new FileInputStream("src/main/resources/jms.properties");

			prop.load(inStream);
			sender = new JMSSender(prop);
			for (int i = 0; i < 10; i++) {
				sender.sendMessage(java.util.UUID.randomUUID().toString());
			}
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

}
