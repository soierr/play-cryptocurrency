# https://hub.docker.com/r/azul/zulu-openjdk
# https://hub.docker.com/layers/zulu-openjdk/azul/zulu-openjdk/11.0.16-11.58.15/images/sha256-90bdadff025dde220f2ba3f26f22102e9b655bf13279cc9dd8638d55c6edcd96?context=explore
FROM azul/zulu-openjdk:11.0.16-11.58.15
RUN cd / && mkdir -p "/opt"

# dynamic versioning needed
COPY ./build/libs/play-cryptocurrency-1.0-SNAPSHOT.jar /opt

RUN mkdir /opt/prices
COPY ./prices /opt/prices

# Install Utilities: ps
# Extra investigation needed, though ps installs ok
# https://stackoverflow.com/questions/51023312/docker-having-issues-installing-apt-utils
# --assume-yes == -y ,below
RUN DEBIAN_FRONTEND=noninteractive apt-get -y update && apt-get -y install apt-utils procps
# -------------

CMD ["java", "-jar", "/opt/play-cryptocurrency-1.0-SNAPSHOT.jar"]
