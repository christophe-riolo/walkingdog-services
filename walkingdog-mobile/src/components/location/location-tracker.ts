import { Injectable, NgZone } from '@angular/core';
import { Geolocation, Geoposition, BackgroundGeolocation } from 'ionic-native';
import { Http, Response } from '@angular/http';
import 'rxjs/add/operator/filter';

@Injectable()
export class LocationTracker {

  private watch: any;    
  private lat: number = 0;
  private lng: number = 0;
  private apiUrl: String;
  private tracking: boolean;

  constructor(
    private zone: NgZone,
    private http: Http) {
    this.apiUrl = 'https://walkingdog-services.herokuapp.com/api/location';
    this.tracking = false;
  }

  startTracking() {
    if (!this.tracking) {
      this.backgroundTracking();
      let promise = this.foregroundTracking();
      this.tracking = true;
      return promise;
    } else {
      return new Promise(((resolve, reject) => {resolve()}));
    }

  }

  stopTracking() {
    BackgroundGeolocation.finish();
    this.watch.unsubscribe();
    this.tracking = false;
  }

  hasPosition(): boolean {
    return this.lat != 0 && this.lng != 0;
  }

  // Deals with background tracking, when app is running but not active.
  private backgroundTracking() {
    // Background Tracking options
    let config = {
      desiredAccuracy: 10,
      stationaryRadius: 20,
      distanceFilter: 10, 
      interval: 15000 
    };

    BackgroundGeolocation.configure((location) => {
      // Run update inside of Angular's zone
      this.zone.run(() => {
        this.processPosition(location);
      });
    }, (err) => {
      console.log(err);
    }, config);

    // Turn ON the background-geolocation system.
    BackgroundGeolocation.start();
  }

  // Deals with foreground tracking, when app is running and active.
  private foregroundTracking() {

    let promise = new Promise((resolve, reject) => {
      // Foreground Tracking options
      let options = {
        enableHighAccuracy: true,
        timeout: 10000
      };

      this.watch = Geolocation
      .watchPosition(options)
      .filter((p: any) => p.code === undefined)
      .subscribe((position) => {
        // Runs update inside of Angular's zone
        this.zone.run(() => {
          this.processPosition(<Geoposition> position);
          resolve();
        });
      });
    });
    return promise;
  }

  private processPosition(position: Geoposition) {
    console.log(position);
    this.lat = position.coords.latitude;
    this.lng = position.coords.longitude;
    this.registerMyLocation();
  }

  private registerMyLocation() {
    // Sends location of current user to server, to be used by others
    this.http.post(
      `${this.apiUrl}/register`,
      JSON.stringify({
        // TODO : current user id
        id: 'azertyuiop', 
        // TODO : current user dog
        name: 'My Dog',
        latitude: this.lat,
        longitude: this.lng,
      }))
    .subscribe((res: Response) => {
      console.log(res);
    });
  }

  public getLat(): number {
    return this.lat;
  }

  public getLng(): number {
    return this.lng;
  }

}