package edu.nwmissouri.KafkaLiveScoreStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

/**
 * Custom LiveScoreKafkaConsumer using Kafka for messaging.
 * Reads properties from the run.properties file in
 * src/main/resources.
 */
public class LiveScoreKafkaConsumer {
    private static Scanner in;
    private static FileInputStream runStream = null;
    private static Properties runProperties = new Properties();

    static MongoClient mongo = new MongoClient("localhost", 27017);
    // Accessing the database
    static MongoDatabase database = mongo.getDatabase("myDb");
    // Retrieving a collection
    static MongoCollection<Document> collection = database.getCollection("sampleCollection");

    static JSONParser parser;
    static JSONObject json;
    static Document doc;

    public static void main(String[] argv) throws Exception {

        MongoClient mongo = new MongoClient("localhost", 27017);

        System.out.println("Connected to the database successfully");

        // Accessing the database

        MongoDatabase database = mongo.getDatabase("myDb");
//
//        // Retrieving a collection
//        MongoCollection collection = database.getCollection("sampleCollection");
        // Create an input stream for the run properties ................
        String runFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "consumer.properties";
        System.out.println("Reading config from " + runFile);
        runStream = new FileInputStream(runFile);

        // Load properties and display
        runProperties.load(runStream);
        System.out.println("Run properties.................");
        System.out.println("BOOTSTRAP_SERVERS_CONFIG =      " + runProperties.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        System.out
                .println("KEY_DESERIALIZER_CLASS_CONFIG =   " + runProperties.getProperty("KEY_DESERIALIZER_CLASS_CONFIG"));
        System.out
                .println("VALUE_DESERIALIZER_CLASS_CONFIG = " + runProperties.getProperty("VALUE_DESERIALIZER_CLASS_CONFIG"));
        System.out.println("TOPIC =                           " + runProperties.getProperty("TOPIC"));
        System.out.println("GROUPID =                         " + runProperties.getProperty("GROUPID"));
        System.out.println("CLIENT_ID_CONFIG =                " + runProperties.getProperty("CLIENT_ID_CONFIG"));
        System.out.println("TIMEOUT_MS =                      " + runProperties.getProperty("TIMEOUT_MS"));

        in = new Scanner(System.in);
        String topicName = runProperties.getProperty("TOPIC");
        String groupId = runProperties.getProperty("GROUPID");
        int timeout_ms = Integer.parseInt(runProperties.getProperty("TIMEOUT_MS"));

        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                runProperties.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                runProperties.getProperty("KEY_DESERIALIZER_CLASS_CONFIG"));
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                runProperties.getProperty("VALUE_DESERIALIZER_CLASS_CONFIG"));
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, runProperties.getProperty("CLIENT_ID_CONFIG"));

        ConsumerThread consumerRunnable = new ConsumerThread(configProperties, topicName, timeout_ms);
        consumerRunnable.start();
        String line = "";
        while (!line.equals("exit")) {
            line = in.next();
        }
        consumerRunnable.getKafkaConsumer().wakeup();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends Thread {
        static ObjectMapper objectMapper;
        private Properties configProperties;
        private String topicName;
        private int timeout_ms;
        private KafkaConsumer<String, CustomObject> kafkaConsumer;

        public ConsumerThread(Properties configProperties, String topicName, int timeout_ms) {
            this.configProperties = configProperties;
            this.topicName = topicName;
            this.timeout_ms = timeout_ms;
        }

        public void run() {
            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<String, CustomObject>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));
            //Start processing messages

            try {
                while (true) {
                    ConsumerRecords<String, CustomObject> records = kafkaConsumer.poll(timeout_ms);
                    int i = 0;
                    for (ConsumerRecord<String, CustomObject> record : records) {

                        System.out.println("Country Name:" + record.value().getCountryName());
                        String stringToParse = record.value().getCountryName();
                        doc = Document.parse(stringToParse);
                        collection.insertOne(doc);
                    }
                }
            } catch (WakeupException ex) {
                System.out.println("Exception caught " + ex.getMessage());
            } finally {
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }

        public KafkaConsumer<String, CustomObject> getKafkaConsumer() {
            return this.kafkaConsumer;
        }
    }


    public static void mongoDBMethod() {
        MongoClient mongo = new MongoClient("localhost", 27017);

        System.out.println("Connected to the database successfully");

        // Accessing the database
        MongoDatabase database = mongo.getDatabase("myDb");

        // Retrieving a collection
        MongoCollection collection = database.getCollection("sampleCollection");
    }
}
