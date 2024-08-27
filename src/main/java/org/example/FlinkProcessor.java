package org.example;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.redis.RedisSink;

public class FlinkProcessor {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.fromData("apple", "banana", "error", "cherry", "date");
        dataStream.sinkTo(new RedisSink());
        env.execute("Flink Redis Sink Example");
    }
}
