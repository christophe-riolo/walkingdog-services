import { Component,ViewChild,ElementRef } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LocationTracker } from '../../../components/location/location-tracker';
import {Http, Response} from '@angular/http';


// Comes from Google Maps JavaScript API. See index.html
declare var google;

@Component({
  selector: 'map-tab',
  templateUrl: 'map-tab.html'
})
export class MapTab {

  // Stores Google map
  map: any;
  // Html element map to display Google map
  @ViewChild('map') mapElement: ElementRef;
  // Stores the marker of the current user
  currentUserMarker: any;
  // Stores the markers of pets around
  petsAroundMarkers: Array<any>;

  private apiUrl: String;

  constructor(
    public navCtrl: NavController, 
    public locationTracker: LocationTracker,
    private http: Http) {
    this.apiUrl = 'https://walkingdog-services.herokuapp.com/api/map/dogsAround';
    this.petsAroundMarkers = [];
  }
  
  // http://www.joshmorony.com/ionic-2-how-to-use-google-maps-geolocation-video-tutorial/
  // https://forum.ionicframework.com/t/blank-google-maps-after-navigation/51104/13
  // http://stackoverflow.com/questions/39922627/issue-with-google-maps-javascript-api-and-ionic-2
  // https://github.com/driftyco/ionic-conference-app/blob/master/src/pages/map/map.ts

  ionViewDidLoad() {
    this.loadMap();
    this.track();
    
  }

  ionViewDidEnter() {
    
  }

  track() {
    // Refresh map every n secondes
    setInterval(() => {
      // Deletes previous marker
      if (this.currentUserMarker) {
        this.currentUserMarker.setMap(null);
      }
      // Creates and stores a new marker with the current position.
      this.currentUserMarker = this.addMarker("you", "You", this.locationTracker.lat, this.locationTracker.lng, null);

      // Find dogs around
      this.showNearestPets();

    }, 10000);
  }

  loadMap() {
 
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

    // Centering the map on the user current position.
    let currentPosition = new google.maps.LatLng(this.locationTracker.lat, this.locationTracker.lng);
    this.map.setCenter(currentPosition);
    this.currentUserMarker = this.addMarker("you", "You", this.locationTracker.lat, this.locationTracker.lng, null);
  }

  private addMarker(dogId: string, dogName: string, lat: number, lng : number, icon: string): any {
    let currentPosition = {lat: lat, lng: lng};
    let marker = new google.maps.Marker({
      map: this.map,
      animation: google.maps.Animation.DROP,
      position: currentPosition,
      icon: icon
    });
    let content = `<h4>${dogName}</h4>`;          
    this.addInfoWindow(marker, content);
    return marker;
  }

  addInfoWindow(marker, content){
    let infoWindow = new google.maps.InfoWindow({
      content: content
    });
    google.maps.event.addListener(marker, 'click', () => {
      infoWindow.open(this.map, marker);
    });
  }

  private showNearestPets() {
    // Prepares the request
    let params: string = [
      `ne-lat=${this.map.getBounds().getNorthEast().lat()}`,
      `ne-lon=${this.map.getBounds().getNorthEast().lng()}`,
      `sw-lat=${this.map.getBounds().getSouthWest().lat()}`,
      `sw-lon=${this.map.getBounds().getSouthWest().lng()}`
    ].join('&');
    // Sends request to backend
    this.http.request(`${this.apiUrl}?${params}`)
      .subscribe((res: Response) => {
        console.log(res.json());
        // Removes previous markers
        for (let marker of this.petsAroundMarkers) {
          marker.setMap(null);
        }
        // Clears array
        this.petsAroundMarkers = []; 
        // Creates new markers
        for (let pet of res.json()) {
          // Adds new marker
          let marker = this.addMarker(pet.id, pet.name, pet.latitude, pet.longitude, 'assets/icon/pets.png');
          // Keeps reference of created marker
          this.petsAroundMarkers.push(marker);
        }
      });
  }

}
