.PHONY: gen clean

gen:
	docker compose -p sabd up -d

gen_s:
	@if [ $(words $(MAKECMDGOALS)) -ne 2 ]; then     \
	  echo "Usage: make gen_s <num_taskmanagers>"; \
	  exit 1; \
	fi; \
	TM=$(word 2,$(MAKECMDGOALS)); \
	docker compose -p sabd up -d --scale taskmanager=$$TM

clean:
	docker compose -p sabd down -v