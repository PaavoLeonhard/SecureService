#! /bin/sh

MINIO_ACCESS_KEY="$(openssl rand -base64 21)"
MINIO_ACCESS_KEY="$MINIO_ACCESS_KEY" | tr = a
export MINIO_ACCESS_KEY
MINIO_SECRET_KEY="$(openssl rand -base64 33)"
MINIO_SECRET_KEY="$MINIO_SECRET_KEY" | tr = a
export MINIO_SECRET_KEY



./minio server /mnt/data &
echo $MINIO_ACCESS_KEY &
exec mvn spring-boot:run  
