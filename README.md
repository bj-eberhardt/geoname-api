# Cities API

Offers an API to get from a known location (geo: lat, lng) the nearest cities without
any network connection.

All data is stored locally in a SQL database.

It's using the geonames database from: https://download.geonames.org/export/zip/

If you prefer a smaller dataset, checkout [geo-city-api](https://github.com/bj-eberhardt/geo-city-api)

## Checkout

Checkout via https://git-lfs.com/ as there is a dump file containing all cities part
of this repository for the initial dump.

## Development

Deploy a local mysql database, for example via docker: 
`docker run --env=MYSQL_DATABASE=cities --env=MYSQL_USER=cities --env=MYSQL_PASSWORD=password --env=MYSQL_ROOT_PASSWORD=password  --volume=/var/lib/mysql -p 33060:3306 --restart=no -d mysql:8.0`
that exposes the port 33060 on localhost, otherwise modify the file _"[application.properties](src/main/resources/application.properties)"_.

Start using ```./gradle run```. The server is available at http://127.0.0.1.8080/


### Update city list

Call the API Endpoint `
curl -X 'POST' \
'http://localhost:8080/database/update' \
-H 'accept: application/json' \
-d ''
`

This will update the database in a transaction so that the API still works with the old data.
Be aware that the update will take about 1 hour.
You can also dump the database afterward and create a new update script in "db/migration" folder,
so that it is executed on next run, even if you lost the mysql database data.


## Building

Build using ```./gradle dockerBuild``` or as native image ```./gradle dockerBuildNative```.

It's creating a docker image named: _geo-city-api:latest_ 

## Deploying

Use the docker compose file in  [geonamesapi-stack](geonames-stack/docker-compose.yml).
It creates a new stack exposing the api on http://127.0.0.1.15000/ and also starts a mysql database.
The first start will take up to 10 minutes to insert the dump.

More information in [docker compose stack info](geonames-stack/README.md)


