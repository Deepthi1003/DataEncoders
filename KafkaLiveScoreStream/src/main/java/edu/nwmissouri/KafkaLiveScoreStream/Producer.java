package edu.nwmissouri.KafkaLiveScoreStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Custom Producer using Kafka for messaging.
 * Reads properties from the run.properties file in
 * src/main/resources.
 */
public class Producer {
    private static FileInputStream runStream = null;
    private static Properties runProperties = new Properties();

    public static void main(String[] argv) throws Exception {


        // Create an input stream for the run properties ................
        String runFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "run.properties";
        System.out.println("Reading config from " + runFile);
        runStream = new FileInputStream(runFile);

        // Load properties and display
        runProperties.load(runStream);


        String topicName = runProperties.getProperty("TOPIC");
        int delay_ms = Integer.parseInt(runProperties.getProperty("DELAY_MS"));

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                runProperties.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                runProperties.getProperty("KEY_SERIALIZER_CLASS_CONFIG"));
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                runProperties.getProperty("VALUE_SERIALIZER_CLASS_CONFIG"));
        org.apache.kafka.clients.producer.Producer<String, edu.nwmissouri.KafkaLiveScoreStream.CustomObject> producer = new KafkaProducer<String, edu.nwmissouri.KafkaLiveScoreStream.CustomObject>(configProperties);

        System.out.println("==========================================");
        System.out.println("You must start a consumer to see messages.");
        System.out.println("==========================================");
        System.out.println("\nStarting producer..............\n");



        while (true) {

        CricBuzzParser cbp = new CricBuzzParser("http://mapps.cricbuzz.com/cbzios/match/livematches");
        cbp.RetrieveURL();
        String sendScore = cbp.Parse1();
            edu.nwmissouri.KafkaLiveScoreStream.CustomObject co = new edu.nwmissouri.KafkaLiveScoreStream.CustomObject(sendScore);
            ProducerRecord<String, edu.nwmissouri.KafkaLiveScoreStream.CustomObject> rec = new ProducerRecord<>(topicName, co);
            System.out.println(sendScore);
            producer.send(rec);
            Thread.sleep(120000 / 4);
        }
    }

}
