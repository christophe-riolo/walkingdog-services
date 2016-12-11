import { Component,ViewChild,ElementRef } from '@angular/core';
import { LoadingController } from 'ionic-angular';
import { NavController } from 'ionic-angular';
import { Geolocation } from 'ionic-native';
import { LocationTracker } from '../../../components/location/location-tracker';
import { Http, Response } from '@angular/http';


// Comes from Google Maps JavaScript API. See index.html
declare var google;

@Component({
  selector: 'map-tab',
  templateUrl: 'map-tab.html'
})
export class MapTab {

  // Stores Google map
  private map: any;
  // Html element map to display Google map
  @ViewChild('map') mapElement: ElementRef;
  // Stores the marker of the current user
  currentUserMarker: any;
  // Stores the markers of pets around
  petsAroundMarkers: Array<any>;
  // Tells if user is walking
  walking: boolean;

  private apiUrl: String;
  private geoError: boolean;
  private loader: any;

  constructor(
    private navCtrl: NavController, 
    private locationTracker: LocationTracker,
    private http: Http,
    private loadingCtrl: LoadingController) {
    this.apiUrl = 'https://walkingdog-services.herokuapp.com/api/location';
    //this.apiUrl = 'http://localhost:8080/api/location';
    this.petsAroundMarkers = [];
    //
    this.walking = false;
  }
  
  // http://www.joshmorony.com/ionic-2-how-to-use-google-maps-geolocation-video-tutorial/
  // https://forum.ionicframework.com/t/blank-google-maps-after-navigation/51104/13
  // http://stackoverflow.com/questions/39922627/issue-with-google-maps-javascript-api-and-ionic-2
  // https://github.com/driftyco/ionic-conference-app/blob/master/src/pages/map/map.ts

  ionViewDidLoad() {
    // Loads Google Map
    this.loadMap();
    // Starts tracking of the user
    this.locationTracker.startTracking();
    // Init map
    this.init();    
  }

  // Called by locate me button to center the map on user location
  center() {
    // Centers the map on the user current position.
    let currentPosition = new google.maps.LatLng(this.locationTracker.getLat(), this.locationTracker.getLng());
    this.map.setCenter(currentPosition);
  }

  startWalk() {
    this.walking = true;
  }

  stopWalk() {
    this.walking = false;
  }

  private init() {
    this.geoError = false;

    this.loader = this.loadingCtrl.create({
      content: "Loading current position..."
    });
    this.loader.present();

    // Init user location
    Geolocation.getCurrentPosition().then((position) => {
      // Stores position
      let currentLat = position.coords.latitude;
      let currentLng = position.coords.longitude;
      // Centers the map on the user current position.
      let currentPosition = new google.maps.LatLng(currentLat, currentLng);
      this.map.setCenter(currentPosition);
      // Adds marker on current user position
      this.currentUserMarker = this.addMarker("you", "You", currentLat, currentLng, null);
      // Deals with current user location
      this.updateMyLocation();
      // Find dogs around
      this.showPetsInMap();
      // Removes the loader
      this.loader.dismiss();

      // Refresh map every 10 seconds
      setInterval(() => {
        // Deals with current user location
        this.updateMyLocation();
        // Find dogs around
        this.showPetsInMap();

      }, 10000);
    }, (err) => {
      this.geoError = true;
      this.loader.dismiss();
    });

  }

  private loadMap() {
    // Loading a map with default position.
    let latLng = new google.maps.LatLng(51.528308, -0.3817765,10);
    let mapOptions = {
      center: latLng,
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      zoomControl: false,
      mapTypeControl: false,
      scaleControl: false,
      streetViewControl: false,
      rotateControl: false,
      fullscreenControl: false
    }
    this.map = new google.maps.Map(this.mapElement.nativeElement, mapOptions);
    google.maps.event.addListenerOnce(this.map, 'idle', () => { 
      this.mapElement.nativeElement.classList.add('show-map'); 
    });
  }

  private updateMyLocation() {
    // Removes previous user location
    if (this.currentUserMarker) {
      this.currentUserMarker.setMap(null);
    }
    // Creates and stores a new marker with the current position.
    this.currentUserMarker = this.addMarker("you", "You", this.locationTracker.getLat(), this.locationTracker.getLng(), null);
  }

  private showPetsInMap() {
    // Prepares the request
    let params: string = [
      `ne-lat=${this.map.getBounds().getNorthEast().lat()}`,
      `ne-lon=${this.map.getBounds().getNorthEast().lng()}`,
      `sw-lat=${this.map.getBounds().getSouthWest().lat()}`,
      `sw-lon=${this.map.getBounds().getSouthWest().lng()}`
    ].join('&');
    // Sends request to backend
    this.http.request(`${this.apiUrl}/dogsAround?${params}`)
      .subscribe((res: Response) => {
        // Removes previous markers
        for (let marker of this.petsAroundMarkers) {
          marker.setMap(null);
        }
        // Clears array
        this.petsAroundMarkers = []; 
        // Creates new markers
        for (let pet of res.json()) {
          if (pet.id !== 'azertyuiop') { // Filters the marker of the user
            // Adds new marker
            let marker = this.addMarker(pet.id, pet.name, pet.latitude, pet.longitude, 'assets/icon/pets.png');
            // Keeps reference of created marker
            this.petsAroundMarkers.push(marker);
          }
        }
      });
  }

  private addMarker(dogId: string, dogName: string, lat: number, lng : number, icon: string): any {
    let currentPosition = {lat: lat, lng: lng};
    let marker = new google.maps.Marker({
      map: this.map,
      //animation: google.maps.Animation.DROP,
      position: currentPosition,
      icon: icon
    });
    let content = `<h4>${dogName}</h4>`;          
    this.addInfoWindow(marker, content);
    return marker;
  }

  private addInfoWindow(marker, content){
    let infoWindow = new google.maps.InfoWindow({
      content: content
    });
    google.maps.event.addListener(marker, 'click', () => {
      infoWindow.open(this.map, marker);
    });
  }

}
