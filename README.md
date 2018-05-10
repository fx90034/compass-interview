crawler\target>java -cp ./Crawler-0.0.1-SNAPSHOT.jar cybercoders.Crawler or

crawler\target>java -cp ./Crawler-0.0.1-SNAPSHOT.jar cybercoders.FastCrawler




Using multi-threading should be faster for large volume of queries. But with this example of the JSON data, the sequential is faster. Maybe because the overhead of multi-threading with Callable tasks that takes more time to wait each thread is done.