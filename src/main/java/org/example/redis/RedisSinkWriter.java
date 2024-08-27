package org.example.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.flink.api.connector.sink2.SinkWriter;

import java.io.IOException;

public class RedisSinkWriter implements SinkWriter<String> {

    private final transient RedisClient redisClient;
    private final transient StatefulRedisConnection<String, String> connection;
    private final transient RedisCommands<String, String> syncCommands;

    public RedisSinkWriter(RedisClient redisClient, StatefulRedisConnection<String, String> connection, RedisCommands<String, String> syncCommands) {
        this.redisClient = redisClient;
        this.connection = connection;
        this.syncCommands = syncCommands;
    }

    @Override
    public void write(String element, Context context) throws IOException {
        int attempt = 0;
        boolean success = false;

        int maxRetries = 3;
        while (!success) {
            try {
                if (syncCommands == null) {
                    throw new IOException("Redis connection is not initialized.");
                }

                if ("error".equals(element)) {
                    throw new IOException("Simulated error for testing.");
                }

                syncCommands.rpush("flink-data", element);
                success = true;
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new IOException("Failed to write data to Redis after " + maxRetries + " attempts.", e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Thread interrupted during retry wait.", ie);
                }
            }
        }
    }

    @Override
    public void flush(boolean endOfInput) {
        // Redis는 데이터가 즉시 저장되기 때문에 특별한 flush 로직은 필요하지 않을 수 있음
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }

}