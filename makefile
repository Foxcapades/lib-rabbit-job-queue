.PHONY: nothing
nothing:
	@echo "Pick a target"

.PHONY: end-to-end
end-to-end:
	@docker compose -f test/docker-compose.yml build \
		--build-arg=GITHUB_USERNAME=$(shell grep 'gpr.user' ~/.gradle/gradle.properties | cut -d= -f2) \
		--build-arg=GITHUB_TOKEN=$(shell grep 'gpr.key' ~/.gradle/gradle.properties | cut -d= -f2)
	@docker compose -f test/docker-compose.yml up --no-attach rabbit

.PHONY: kill-tests
kill-tests:
	@docker compose -f test/docker-compose.yml down --rmi local -v

.PHONY: docs
docs:
	@./gradlew dokkaHtml
	@rm -rf docs/dokka
	@cp -r build/dokka/html docs/dokka
	@cp docs/logo-styles.css docs/dokka/styles/logo-styles.css
