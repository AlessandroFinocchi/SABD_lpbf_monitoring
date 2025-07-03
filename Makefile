.PHONY: q gen clean

PRE_GEN:
	if [ ! -f "micro-challenger/gc25cdocker.tar" ]; then \
		cd micro-challenger && unzip gc25cdocker.zip && cd ..; \
	fi && \
	mvn clean package && \
	docker image load -i micro-challenger/gc25cdocker.tar && \
	mkdir -p -m 777 results/queries results/metrics performance_analyzer/input

# rm -Rf /metrics/* && rm -Rf /results/*
q:
	mvn package
	docker exec jobmanager /opt/flink/bin/flink run /flink-monitor-jar/flink-monitor-1.0-SNAPSHOT.jar

gen: PRE_GEN
	docker compose -p sabd up -d

gen_s: PRE_GEN
	docker compose -p sabd up -d --scale taskmanager=$(TM)

clean:
	docker compose -p sabd down -v
	docker images | grep "sabd*" | awk '{print $3}' | xargs docker rmi
	docker images | grep "micro-challenger*" | awk '{print $3}' | xargs docker rmi