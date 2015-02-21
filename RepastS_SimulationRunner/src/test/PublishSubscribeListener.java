package test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;

import redis.clients.jedis.JedisPubSub;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving publishSubscribe events. The class that
 * is interested in processing a publishSubscribe event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addPublishSubscribeListener<code> method. When
 * the publishSubscribe event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PublishSubscribeEvent
 */
public class PublishSubscribeListener extends JedisPubSub {

	/**
	 * Sets the up before class.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tear down after class.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/** The redis connection manager. */
	private RedisConnectionManager redisConnectionManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onMessage(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void onMessage(String channel, String message) {
		System.out.println("Received message" + message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onPMessage(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void onPMessage(String pattern, String channel, String message) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onPSubscribe(java.lang.String, int)
	 */
	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onPUnsubscribe(java.lang.String,
	 * int)
	 */
	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onSubscribe(java.lang.String, int)
	 */
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see redis.clients.jedis.JedisPubSub#onUnsubscribe(java.lang.String, int)
	 */
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
	}

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test.
	 */
	@Test
	public void test() {
		redisConnectionManager = new RedisConnectionManager();
		redisConnectionManager.initializeRedisConnection("localhost");
		redisConnectionManager.getJedis();

		// LOW: Future work, after implementing multithreading for Redis
		// Subscribe listener
		// PublishSubscribeListener l = new PublishSubscribeListener();
		// jedis.subscribe(l,
		// "csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff");

		// TODO: remove after reading?
		// Rely on Polling for now, keep everything Synchronous and simple.
		redisConnectionManager
		.redisSynchronousPolling(
				SYSTEM_TYPE.SIMULATION_ENGINE,
				"csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff",
				0.001d, null);

		// Test that connection works after 300 second timeout.
		/*
		 * redisConnectionManager.redisSynchronousPolling(
		 * "csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff",
		 * 1l, 1l); System.out.println("finished first call"); try {
		 * Thread.sleep(350*1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * redisConnectionManager.redisSynchronousPolling(
		 * "csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff",
		 * 1l, 1l);
		 */

		System.out.println("Finished reading");
	}

}
