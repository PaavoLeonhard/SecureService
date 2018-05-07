#! /bin/sh

export MINIO_ACCESS_KEY="$(openssl rand -base64 20)"
export MINIO_SECRET_KEY="$(openssl rand -base64 32)"

./minio server /mnt/data &
echo $MINIO_ACCESS_KEY &
exec mvn spring-boot:run  