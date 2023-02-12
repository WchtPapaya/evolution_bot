FROM alpine:3.17
RUN apk update && apk add openjdk11-jre
WORKDIR /evolution_bot
COPY ./bin ./bin
COPY ./config ./config
COPY ./update ./update
COPY ./evolution_bot.jar .
CMD  ["/evolution_bot/bin/start.sh"]