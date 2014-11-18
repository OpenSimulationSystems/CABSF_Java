package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;
import org.simulationsystems.csf.common.internal.messaging.interfaces.redis.RedisConnectionManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class PublishSubscribeListener extends JedisPubSub {
	private RedisConnectionManager redisConnectionManager;

	public void onMessage(String channel, String message) {
		System.out.println("Received message" + message);
	}

	public void onSubscribe(String channel, int subscribedChannels) {

	}

	public void onUnsubscribe(String channel, int subscribedChannels) {
	}

	public void onPSubscribe(String pattern, int subscribedChannels) {
	}

	public void onPUnsubscribe(String pattern, int subscribedChannels) {
	}

	public void onPMessage(String pattern, String channel, String message) {
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		redisConnectionManager = new RedisConnectionManager();
		redisConnectionManager.initializeRedisConnection("localhost");
		Jedis jedis = redisConnectionManager.getJedis();
		
		//LOW: Future work, after implementing multithreading for Redis Subscribe listener
		//PublishSubscribeListener l = new PublishSubscribeListener();
		//jedis.subscribe(l, "csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff");
		
		//TODO: remove afte reading?
		//Rely on Polling for now, keep everything Synchronous and simple.
		redisConnectionManager.redisSynchronousPolling(SYSTEM_TYPE.SIMULATION_ENGINE, "csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff", 1l, null);
		
		//Test that connection works after 300 second timeout.
/*		redisConnectionManager.redisSynchronousPolling("csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff", 1l, 1l);
		System.out.println("finished first call");
		try {
			Thread.sleep(350*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		redisConnectionManager.redisSynchronousPolling("csf.commands.simToDistSystem:19def3fa-a1d4-4996-a1ac-22c3a041e6ff", 1l, 1l);
*/
		
		System.out.println("Finished reading");
	}

}
