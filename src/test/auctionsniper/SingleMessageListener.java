package test.auctionsniper;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

public class SingleMessageListener implements MessageListener {
	private static final TimeUnit SECONDS = TimeUnit.SECONDS;
	private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}
	
	public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException{
		final Message message = messages.poll(5, SECONDS);
		assertThat(message, hasProperty("body", messageMatcher));
	}
	
	public void receivesAMessage() throws InterruptedException{
		assertThat("Message", messages.poll(5, SECONDS), is(notNullValue()));
	}
}
