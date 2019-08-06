===Compile===
gradle clean build
copy build/libs/jsprofile-0.1.0.jar 

Copy application.properties, logback.xml and jsprofile-0.1.0.jar

Dev
java -jar jsprofile-0.1.0.jar 

QA
java -jar -Dspring.profiles.active=qa jsprofile-0.1.0.jar  

or
./jsprofile-0.1.0.jar

live 
java -jar -Dspring.profiles.active=live jsprofile-0.1.0.jar --logging.config=./logback.xml

or
./jsprofile-0.1.0.jar

open below link in browser
http://localhost:9190/swagger-ui.html


==============
Centralized caching

1> make sure the jsprofile.properties is pushed in JsConfig
2> run JsConfigServer on 8999 port

