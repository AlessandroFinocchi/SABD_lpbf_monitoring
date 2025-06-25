.PHONY: q gen clean

q:
	#docker exec jobmanager ls -l /flink-monitor-jar
	docker exec jobmanager /opt/flink/bin/flink run /flink-monitor-jar/flink-monitor-1.0-SNAPSHOT.jar
	#docker exec jobmanager cat /opt/flink/log/flink--client-jobmanager.log
	#docker exec jobmanager cat /opt/flink/log/flink--standalonesession-0-jobmanager.log


gen:
	docker image load -i micro-challenger/gc25cdocker.tar
	docker compose -p sabd up -d

gen_s:
	@if [ $(words $(MAKECMDGOALS)) -ne 2 ]; then     \
	  echo "Usage: make gen_s <num_taskmanagers>"; \
	  exit 1; \
	fi; \
	TM=$(word 2,$(MAKECMDGOALS)); \
	docker image load -i micro-challenger/gc25cdocker.tar
	docker compose -p sabd up -d --scale taskmanager=$$TM

clean:
	docker compose -p sabd down -v
	docker images | grep "sabd*" | awk '{print $3}' | xargs docker rmi
	docker images | grep "micro-challenger*" | awk '{print $3}' | xargs docker rmi