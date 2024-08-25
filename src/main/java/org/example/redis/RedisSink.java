package org.example.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

public class RedisSink implements Sink<String> {


    @Override
    @Deprecated
    public SinkWriter<String> createWriter(InitContext context) {
        return null;
    }

    @Override
    public SinkWriter<String> createWriter(WriterInitContext context) {
        final RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        final StatefulRedisConnection<String, String> connection = redisClient.connect();
        final RedisCommands<String, String> syncCommands = connection.sync();

        return new RedisSinkWriter(redisClient, connection, syncCommands);
    }

}
