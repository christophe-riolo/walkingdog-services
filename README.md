Walking Dog
====
[![Build Status](https://travis-ci.org/paoesco/walkingdog.svg?branch=master)](https://travis-ci.org/paoesco/walkingdog)

## API Documentation

### Location API

- Dogs around
`GET /api/location/dogsAround?ne-lat=51.603176&ne-lon=-0.187197&sw-lat=51.599313&sw-lon=-0.199326`

- Register location
`POST /api/location/register  { "id":"dog1", "name":"Dog 1", "latitude":51.600000, "longitude":-0.190000 }`

## Environment

- Integration : https://walkingdog-services.herokuapp.com/

## Contribute

- Install nodejs
- npm install -g ionic
- npm install -g cordova
- git clone https://github.com/paoesco/walkingdog.git
- cd walkingdog
- git config user.name "xxx"
- git config user.email "xxx@xxx.com"
- npm install
- ionic platform add android
- Install Android Studio