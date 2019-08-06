FROM dockerjs.infoedge.com:5000/centosjava:v1
MAINTAINER Tushar Gandhi <tushar.gandhi@jeevansathi.com>

EXPOSE 18290
EXPOSE 18292

RUN mkdir -p /home/JsRuleEngine /home/logs
RUN chmod -R 777 /home/JsRuleEngine /home/logs

ADD build/libs/jsruleengine.jar /home/JsRuleEngine
ADD application-docker.properties /home/JsRuleEngine
RUN mv /home/JsRuleEngine/application-docker.properties /home/JsRuleEngine/application.properties
ADD logback.xml /home/JsRuleEngine

CMD cd /home/JsRuleEngine/ && java -Xms256m -Xmx256m -jar jsruleengine.jar
#docker build --rm --network=host -t jsruleengine.jar:v1 .
#docker run -it --net=host jsruleengine.jar:v1
#image pushed by name of docker.js.infoedge.com:5000/jsruleengine.jar:v1
# log path /home/logs/jsruleengine.log
