"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var ionic_native_1 = require('ionic-native');
require('rxjs/add/operator/filter');
var LocationTracker = (function () {
    function LocationTracker(zone) {
        this.zone = zone;
        this.lat = 0;
        this.lng = 0;
    }
    LocationTracker.prototype.startTracking = function () {
        var _this = this;
        // Init
        ionic_native_1.Geolocation.getCurrentPosition(function (location) {
            _this.lat = location.coords.latitude;
            _this.lng = location.coords.longitude;
        });
        // Every 15 seconds
        this.backgroundTracking();
        // Every 15 seconds
        this.foregroundTracking();
    };
    LocationTracker.prototype.stopTracking = function () {
        console.log('stopTracking');
        ionic_native_1.BackgroundGeolocation.finish();
        this.watch.unsubscribe();
    };
    LocationTracker.prototype.hasPosition = function () {
        return this.lat != 0 && this.lng != 0;
    };
    LocationTracker.prototype.backgroundTracking = function () {
        var _this = this;
        // Background Tracking
        var config = {
            desiredAccuracy: 0,
            stationaryRadius: 20,
            distanceFilter: 10,
            debug: true,
            interval: 15000
        };
        ionic_native_1.BackgroundGeolocation.configure(function (location) {
            console.log('BackgroundGeolocation:  ' + location.latitude + ',' + location.longitude);
            // Run update inside of Angular's zone
            _this.zone.run(function () {
                _this.lat = location.latitude;
                _this.lng = location.longitude;
            });
        }, function (err) {
            console.log(err);
            alert('Unable to get current position.');
        }, config);
        // Turn ON the background-geolocation system.
        ionic_native_1.BackgroundGeolocation.start();
    };
    LocationTracker.prototype.foregroundTracking = function () {
        var _this = this;
        // Foreground Tracking
        var options = {
            frequency: 15000,
            enableHighAccuracy: true
        };
        this.watch = ionic_native_1.Geolocation.watchPosition(options).filter(function (p) { return p.code === undefined; }).subscribe(function (position) {
            console.log(position);
            // Run update inside of Angular's zone
            _this.zone.run(function () {
                _this.lat = position.coords.latitude;
                _this.lng = position.coords.longitude;
            });
        });
    };
    LocationTracker = __decorate([
        core_1.Injectable()
    ], LocationTracker);
    return LocationTracker;
}());
exports.LocationTracker = LocationTracker;
