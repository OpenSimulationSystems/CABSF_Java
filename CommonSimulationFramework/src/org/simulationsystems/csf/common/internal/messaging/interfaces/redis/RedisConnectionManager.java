package org.simulationsystems.csf.common.internal.messaging.interfaces.redis;

import org.simulationsystems.csf.common.csfmodel.SYSTEM_TYPE;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Manages the CRUD operations to Redis. This class has no business-level knowledge.
 * 
 * 
 * @author Jorge Calderon
 * @version 0.1
 * @since 0.1
 */
public class RedisConnectionManager {
	// private Jedis jedis=null;
	/** The jedis pool. */
	static private JedisPool jedisPool = null;

	/** The jedis. */
	static private Jedis jedis = null;

	/** The host. */
	private String host;

	/**
	 * Close pool.
	 */
	public void closePool() {
		jedisPool.destroy();
	}

	/**
	 * Gets the jedis.
	 * 
	 * @return the jedis
	 */
	public Jedis getJedis() {
		return jedis;
	}

	/**
	 * Initialize Redis connection.
	 * 
	 * @param host
	 *            the host
	 */
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

	/**
	 * Post message.
	 * 
	 * @param channel
	 *            the channel
	 * @param message
	 *            the message
	 */
	public void postMessage(final String channel, final String message) {
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

	/**
	 * Redis synchronous polling.
	 * 
	 * @param requestingSystemType
	 *            the requesting system
	 * @param redisKey
	 *            the redis key
	 * @param sleepTime
	 *            the number of second to sleep
	 * @param maximumNumberOfPolls
	 *            the maximum number of polls on the Redis key. If null, this will poll
	 *            forever.
	 * @return the string
	 */
	public String redisSynchronousPolling(final SYSTEM_TYPE requestingSystemType,
			final String redisKey, final Double sleepTime, Long maximumNumberOfPolls) {
		String value = null;
		long i = 0;
		long printCount = 0;
		System.out.println("[" + requestingSystemType + "]"
				+ "Attempting Redis lpop on: " + redisKey);
		while (maximumNumberOfPolls == null || maximumNumberOfPolls > 0) {
			if (printCount == 0) {
				if (requestingSystemType == SYSTEM_TYPE.DISTRIBUTED_SYSTEM)
					System.out.print("+");
				else
					System.out.print(".");
			}

			value = jedis.lpop(redisKey);
			if (value == null) {
				try {
					// System.out.println("sleeping: " + sleepTime * 1000);
					// TODO: make the time configurable?
					final Long sleepLong = (long) (sleepTime * 1000);
					Thread.sleep(sleepLong);
				} catch (final InterruptedException e) {
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
}
