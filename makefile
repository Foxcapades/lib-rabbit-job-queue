PROTOC_VERSION = "3.19.1"
PROTOC_URL := "https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/protoc-${PROTOC_VERSION}-linux-x86_64.zip"

.PHONY: nothing
nothing:
	@echo "Pick a target"

.PHONY: end-to-end
end-to-end:
	@docker-compose -f test/docker-compose.yml build \
		--build-arg=GITHUB_USERNAME=$(shell grep 'gpr.user' ~/.gradle/gradle.properties | cut -d= -f2) \
		--build-arg=GITHUB_TOKEN=$(shell grep 'gpr.key' ~/.gradle/gradle.properties | cut -d= -f2)
	@docker-compose -f test/docker-compose.yml up | grep --color=always -v rabbit_1

.PHONY: docs
docs:
	@mkdir -p docs
	@./gradlew dokkaHtml
	@rm -rf docs/*
	@cp -r build/dokka/html docs/dokka
