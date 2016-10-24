import { Component,ViewChild,ElementRef } from '@angular/core';
import { NavController } from 'ionic-angular';

import { Geolocation } from 'ionic-native';

// Comes from Google Maps JavaScript API. See index.html
declare var google;

@Component({
  selector: 'map-tab',
  templateUrl: 'map-tab.html'
})
export class MapTab {

  map: any;
  @ViewChild('map') mapElement: ElementRef;

  constructor(public navCtrl: NavController) {
    
  }
  
  // http://www.joshmorony.com/ionic-2-how-to-use-google-maps-geolocation-video-tutorial/
  // https://forum.ionicframework.com/t/blank-google-maps-after-navigation/51104/13
  // http://stackoverflow.com/questions/39922627/issue-with-google-maps-javascript-api-and-ionic-2
  // https://github.com/driftyco/ionic-conference-app/blob/master/src/pages/map/map.ts

  ionViewDidLoad() {
    this.loadMap();
  }

  ionViewDidEnter() {
    
  }

  loadMap() {
    Geolocation.getCurrentPosition().then((position) => {
      let latLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
      let mapOptions = {
        center: latLng,
        zoom: 15,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      }
      this.map = new google.maps.Map(this.mapElement.nativeElement, mapOptions);

      google.maps.event.addListenerOnce(this.map, 'idle', () => { 
        this.mapElement.nativeElement.classList.add('show-map'); 
      });
    }, (err) => {
      console.log(err);
    });

  }

}
