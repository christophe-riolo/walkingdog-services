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

### Installation

- Install nodejs
- `npm install -g ionic`
- `npm install -g cordova`
- `git clone https://github.com/paoesco/walkingdog.git`
- `cd walkingdog`
- `git config user.name "xxx"`
- `git config user.email "xxx@xxx.com"`
- `npm install`


#### Android platform


- `ionic platform add android` (inside walkingdog-mobile folder)
- Install Java 8 (JDK)
- Add JAVA_HOME variable (path/to/jdk)
- Add $JAVA_HOME/bin to $PATH
- Install Android Studio (SDK and AVD)
- Add ANDROID_HOME variable (path/to/sdk)
- Add $ANDROID_HOME/tools to $PATH
- Add $ANDROID_HOME/platform-tools to $PATH
- Install SDK API 19 (SDK Platform, Google APIs Intel X86 Atom System Image, Sources for Android SDK)
- Install Intel HAXM (if not installed by Android Studio or Android SDK)

#### iOS platform

// TBC


### Build

- `ionic serve`

#### Android

- `ionic build android`
- `ionic emulate android` (needs AVD up and running)

#### iOS

// TBC