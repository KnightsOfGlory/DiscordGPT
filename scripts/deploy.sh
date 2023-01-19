#!/bin/sh
set -e

tag=$(date -u +"%Y-%m-%d-%H-%M-%S")
remote=gcr.io/knights-of-glory-371921/discordgpt:$tag
latest=gcr.io/knights-of-glory-371921/discordgpt:latest

gradle distZip

docker build --platform linux/amd64 --tag $tag --no-cache .
docker tag $tag $remote
docker tag $tag $latest
docker push $remote
docker push $latest

gcloud config set project knights-of-glory-371921
gcloud run deploy discordgpt --image $latest --region us-central1 --platform managed

gcloud beta run revisions list --service discordgpt --region us-central1 \
  | grep -wv "yes" \
  | cut -d " " -f 3 \
  | grep -v "^$" \
  | xargs -L 1 gcloud beta run revisions delete --quiet --region us-central1

date
