package org.simulationsystems.csf.common.internal.messaging.interfaces.redis;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * Manages the CRUD operations to Redis. This class has no business-level knowledge.
 */
public class RedisConnectionManager {
	// private Jedis jedis=null;
	static private JedisPool jedisPool = null;
	static private Jedis jedis = null;
	private String host;

	public void initializeRedisConnection(final String host) {
		// jedis = new Jedis(host);

		// LOW: Configure Jedis Pool options
		// JedisPoolConfig poolConfig = new JedisPoolConfig();
		// poolConfig.setMaxTotal(128);
		// jedisPool = new JedisPool(poolConfig, RedisDBConfig.HOST, RedisDBConfig.PORT,
		// RedisDBConfig.TIMEOUT, RedisDBConfig.PASSWORD);

		jedisPool = new JedisPool(new JedisPoolConfig(), host);
		jedis = jedisPool.getResource();

		// Hold on to the host to re-establish connection in the future, if
		// necessary.
		this.host = host;

		// TODO: Throw exception is Jedis returns null?
	}

	public void postMessage(String channel, String message) {
		// actually using a Redis key for now, not a channel as used in Publish/Subscribe
		jedis.lpush(channel, message);

		// LOW: Future work. Switch to Publish/Subscribe model using multithreading for
		// the separate
		// Redis subscribe listener.
		// jedis.publish(channel, message);

		System.out.println("Posted message to channel: " + channel + " Message: "
				+ message);
		// System.out.println("lpop: " + jedis.lpop(channel));
	}

	/*
	 * @param maximumNumberOfPolls the maximum number of polls on the Redis key. If null,
	 * this will poll forever.
	 * 
	 * @param sleepTime the number of second to sleep
	 */
	public String redisSynchronousPolling(SYSTEM_TYPE requestingSystem, String redisKey,
			Double sleepTime, Long maximumNumberOfPolls) {
		String value = null;
		long i = 0;
		long printCount = 0;
		System.out.println("[" + requestingSystem + "]" + "Attempting lpop on: "
				+ redisKey);
		while (maximumNumberOfPolls == null || maximumNumberOfPolls > 0) {
			if (printCount == 0) {
				if (requestingSystem == SYSTEM_TYPE.DISTRIBUTED_SYSTEM)
					System.out.print("+");
				else
					System.out.print(".");
			}

			value = jedis.lpop(redisKey);
			if (value == null) {
				try {
					// System.out.println("sleeping: " + sleepTime * 1000);
					// TODO: make the time configurable?
					Long sleepLong = (long) (sleepTime * 1000);
					Thread.sleep(sleepLong);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // set interrupt flag
					throw new RuntimeException(
							"Sleep (or wait) in synchronous polling of Redis has been interrupted.",
							e);
				}
			} else {
				System.out.println("Retrieved value: " + value);
				return value;
			}
			// System.out.println("Stopped sleeping");
			if (maximumNumberOfPolls != null)
				maximumNumberOfPolls--;
			i++;
			printCount++;
			if (printCount == 1000)
				printCount = 0;
		}

		return value;
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void closePool() {
		jedisPool.destroy();
	}
}
