To run kafka and zookeeper: 

cd ..

cd .\kafka_2.13-2.6.0\


.\bin\windows\zookeeper-server-start.bat  .\config\zookeeper.properties

.\bin\windows\kafka-server-start.bat  .\config\server.properties

---------- Create topic in KAFKA ------------

bin\windows\kafka-topics.bat --zookeeper localhost:2181 --topic livescore --create --partitions 1 --replication-factor 1

### Before running the project open MongoDB and MongoDB connector to BI
## To run the project: 

mvn clean compile assembly:single

java -cp target/KafkaLiveScoreStream-1.0-SNAPSHOT-jar-with-dependencies.jar edu/nwmissouri/KafkaLiveScoreStream/LiveScoreKafkaProducer

java -cp target/KafkaLiveScoreStream-1.0-SNAPSHOT-jar-with-dependencies.jar edu/nwmissouri/KafkaLiveScoreStream/LiveScoreKafkaConsumer


