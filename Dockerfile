FROM eclipse-temurin:17-jdk-focal

WORKDIR /vaultservice

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

ENV PORT=6010
EXPOSE 6010

CMD ["./mvnw", "spring-boot:run"]