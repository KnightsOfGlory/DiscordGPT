FROM adoptopenjdk/openjdk11:alpine

# dependencies
RUN apk update && \
    apk upgrade && \
    apk add bash

# runtime
COPY build/distributions/DiscordGPT-1.0.zip /
RUN unzip DiscordGPT-1.0.zip
RUN rm DiscordGPT-1.0.zip

# start
ENTRYPOINT ["/DiscordGPT-1.0/bin/DiscordGPT"]
