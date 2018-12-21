FROM	openjdk:8

RUN	apt-get update
RUN	apt-get install --no-install-recommends -y systemd mongodb

ADD	./target /app

ADD	entrypoint.sh /entrypoint.sh
RUN	chmod 755 /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

