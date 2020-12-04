package edu.nwmissouri.KafkaLiveScoreStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class KafkaJsonSerializer implements Serializer<CustomObject> {
    @Override
    public void configure(Map<String, ?> s, boolean arg1) {
    }

    @Override
    public byte[] serialize(String arg0, CustomObject arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}