import { Injectable, NgZone } from '@angular/core';
import { Geolocation, Geoposition, BackgroundGeolocation } from 'ionic-native';
import 'rxjs/add/operator/filter';

@Injectable()
export class LocationTracker {

  public watch: any;    
  public lat: number = 0;
  public lng: number = 0;
  public walking: boolean = false;

  constructor(public zone: NgZone) {

  }

  startTracking() {
    // Every 15 seconds
    this.backgroundTracking();
    // Every 15 seconds
    this.foregroundTracking();
    this.walking = true;
  }

  stopTracking() {
    console.log('stopTracking');
    BackgroundGeolocation.finish();
    this.watch.unsubscribe();
    this.walking = false;
  }

  hasPosition(): boolean {
    return this.lat != 0 && this.lng != 0;
  }

  private backgroundTracking() {
    // Background Tracking
    let config = {
      desiredAccuracy: 0,
      stationaryRadius: 20,
      distanceFilter: 10, 
      debug: true,
      interval: 15000 
    };

    BackgroundGeolocation.configure((location) => {
      console.log('BackgroundGeolocation:  ' + location.latitude + ',' + location.longitude);
      // Run update inside of Angular's zone
      this.zone.run(() => {
        this.lat = location.latitude;
        this.lng = location.longitude;
      });
    }, (err) => {
      console.log(err);
      alert('Unable to get current position.');
    }, config);

    // Turn ON the background-geolocation system.
    BackgroundGeolocation.start();
  }

  private foregroundTracking() {
    // Foreground Tracking
    let options = {
      frequency: 15000, 
      enableHighAccuracy: true
    };

    this.watch = Geolocation.watchPosition(options).filter((p: any) => p.code === undefined).subscribe((position: Geoposition) => {

      // Run update inside of Angular's zone
      this.zone.run(() => {
        this.lat = position.coords.latitude;
        this.lng = position.coords.longitude;
      });

    });

  }

}