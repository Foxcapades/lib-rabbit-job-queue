FROM amazoncorretto:17-alpine3.15

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

COPY gradle/ gradle/
COPY gradlew ./

RUN ./gradlew --version

COPY ["settings.gradle.kts", "build.gradle.kts", "./"]

RUN ./gradlew --no-daemon --info getDeps

COPY src/ src/
COPY test/ test/

RUN ./gradlew --no-daemon --info :test:server:shadowJar

CMD java -jar test/server/build/libs/service.jar