import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { Geolocation } from 'ionic-native';
import { LoadingController } from 'ionic-angular';
import { Dog } from '../../../components/dog/dog.component';
import { LocationTracker } from '../../../components/location/location-tracker';

@Component({
  selector: 'list-tab',
  templateUrl: 'list-tab.html'
})
export class ListTab {

  dogs: Array<Dog>;
  loader: any;


  constructor(
    public navCtrl: NavController,
    public loadingCtrl: LoadingController,
    public locationTracker: LocationTracker) {
    
  }

  ionViewDidLoad() {
    this.loader = this.loadingCtrl.create({
      content: "Loading current position..."
    });
    this.loader.present();

    // Init user location
    Geolocation.getCurrentPosition().then((position) => {
      this.locationTracker.lat = position.coords.latitude;
      this.locationTracker.lng = position.coords.longitude;

      // Find pets near user location
      this.findNearestPets();

      // Removes the loader
      this.loader.dismiss();
    }, (err) => {
      alert("Impossible to retrieve current position. The app won't work properly");
      this.loader.dismiss();
    });
  }

  startWalk() {
    this.locationTracker.startTracking();
    setTimeout(() => {
      this.locationTracker.stopTracking();
    }, 1000*60*20);
  }

  private findNearestPets() {
    this.dogs = new Array();
    let hyumiko: Dog = new Dog('Hyumiko','Acting like a cat. But can bark.', 'assets/dogs/shiba.jpg');
    this.dogs.push(hyumiko);
    let reveur: Dog = new Dog('Reveur','Acting like a carpet.', 'assets/dogs/teckel.jpg');
    this.dogs.push(reveur);
  }

}
