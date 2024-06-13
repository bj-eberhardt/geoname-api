# Geonames API Stack

Offering an API that returns the closest cities around a given geo position.


## Deploy via Docker

Run `docker compose up -d` or `docker-compose up -d` in this directory
to deploy the stack of the application and database.

You can access the API via http://localhost:17000.

You can change the port and the secrets in the [docker-compose.yml](docker-compose.yml).


## Sample

Sample API request:

```
curl -X 'GET' \
  'http://localhost:15000/cities?latitude=53.55&longitude=10.00&limit=10' \
  -H 'accept: application/json'
```

that returns:

``` 
{
  "list": [
    {
      "name": "Hamburg",
      "latitude": 53.5521,
      "longitude": 10.0004,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 0.23499974621434425
    },
    {
      "name": "Hamburg",
      "latitude": 53.5544,
      "longitude": 9.9946,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 0.6054951500269221
    },
    {
      "name": "Hamburg Altstadt",
      "latitude": 53.5453,
      "longitude": 9.9953,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 0.6079036941847707
    },
    {
      "name": "Hamburg Hamburg-Altstadt",
      "latitude": 53.5453,
      "longitude": 9.9953,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 0.6079036941847707
    },
    {
      "name": "Hamburg",
      "latitude": 53.5568,
      "longitude": 9.9898,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.0127783918305762
    },
    {
      "name": "Hamburg",
      "latitude": 53.5502,
      "longitude": 9.9841,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.0506385554894782
    },
    {
      "name": "Hamburg Klostertor",
      "latitude": 53.5439,
      "longitude": 10.0133,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.1100439951899819
    },
    {
      "name": "Hamburg",
      "latitude": 53.5583,
      "longitude": 10.0107,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.162477604348624
    },
    {
      "name": "Hamburg Sankt Georg",
      "latitude": 53.5564,
      "longitude": 10.0144,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.1879804384763943
    },
    {
      "name": "Hamburg",
      "latitude": 53.5481,
      "longitude": 10.0194,
      "stateCode": "HH",
      "stateName": "Hamburg",
      "countryCode": "DE",
      "distance": 1.2989521533563606
    }
  ],
  "size": 10
}
```
