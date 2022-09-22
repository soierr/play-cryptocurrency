# https://hub.docker.com/r/azul/zulu-openjdk
# https://hub.docker.com/layers/zulu-openjdk/azul/zulu-openjdk/11.0.16-11.58.15/images/sha256-90bdadff025dde220f2ba3f26f22102e9b655bf13279cc9dd8638d55c6edcd96?context=explore
FROM azul/zulu-openjdk:11.0.16-11.58.15
RUN cd / && mkdir -p "/opt"

ARG APP_VERSION
COPY ./build/libs/play-cryptocurrency-$APP_VERSION.jar /opt

RUN mkdir /opt/prices
COPY ./prices /opt/prices

# Install Utilities: ps
# Extra investigation needed, though ps installs ok
# https://stackoverflow.com/questions/51023312/docker-having-issues-installing-apt-utils
# --assume-yes == -y ,below
RUN DEBIAN_FRONTEND=noninteractive apt-get -y update && apt-get -y install apt-utils procps
# -------------

RUN /bin/bash -c  "echo \#\!/bin/bash > /opt/start.sh"
RUN /bin/bash -c "echo -e \"\njava -jar /opt/play-cryptocurrency-\"$APP_VERSION\".jar\" >> /opt/start.sh" # concatenation used inside so extra "" needed
RUN /bin/bash -c "echo -e \"\" >> /opt/start.sh" # empty line
RUN chmod u+x /opt/start.sh
CMD ["/opt/start.sh"]
