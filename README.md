Walking Dog
====
- TravisCI : [![Build Status](https://travis-ci.org/hubesco/walkingdog-services.svg?branch=master)](https://travis-ci.org/hubesco/walkingdog-services)
- Codacy : [![Codacy Badge](https://api.codacy.com/project/badge/Grade/b8cbd8954b874c5eb33aa1cd0b7f32c5)](https://www.codacy.com/app/pao-esco/walkingdog-services?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=paoesco/walkingdog&amp;utm_campaign=Badge_Grade)
- Codecov : [![codecov](https://codecov.io/gh/hubesco/walkingdog-services/branch/master/graph/badge.svg)](https://codecov.io/gh/hubesco/walkingdog-services)

## API Documentation

### Location API

- Dogs around
`GET /api/location/dogsAround?ne-lat=51.603176&ne-lon=-0.187197&sw-lat=51.599313&sw-lon=-0.199326`

- Register location
`POST /api/location/register  { "userUuid":"dog1", "dogName":"Dog 1", "latitude":51.600000, "longitude":-0.190000, "walking": "true|false" }`

- Sign Up
`POST /api/authentication/signup { "email":"xxx@xxx.com", "password":"xxx", "dogName":"Dog xxx", "dogGender":"FEMALE|MALE", "dogBreed":"SHIBA_INU", "dogBirthdate":"2015-01-01" }`

- Log In
`POST /api/authentication/login { "email":"xxx@xxx.com", "password":"xxx"}`

- Activate
`GET /api/authentication/activate?token=xxx`


## DevOps

### Platforms

- Integration : https://walkingdog-services-int.herokuapp.com/
- Production : https://walkingdog-services-prod.herokuapp.com/

### Dependencies

- Cloudinary
- Postgres
- New Relic APM
- Papertrail
- SendGrid

### Environment variables

- CLOUDINARY_URL
- JWT_KEYSTORE_PASSWORD
- JWT_KEYSTORE_PATH
- DATABASE_URL
- NEW_RELIC_LICENSE_KEY
- NEW_RELIC_LOG
- PAPERTRAIL_API_TOKEN
- SENDGRID_PASSWORD
- SENDGRID_USERNAME
- WALKINGDOG_AUTHENTICATION_API_URL

## Contribute

### Installation

- Install Java 8
- Install Maven 3
- `mvn clean install`
- `docker run --name walkingdogs-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres`
- Setup schema
- Create setenv.sh in walkingdog-services directory (ignored by git) and add all environment variables
- Run local.sh in walking-services (current folder should be walkingdog-services)
