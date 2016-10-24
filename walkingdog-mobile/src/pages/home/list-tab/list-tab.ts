import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

import { Dog } from '../components/dog.component';
import { LocationTracker } from '../components/location-tracker';

@Component({
  selector: 'list-tab',
  templateUrl: 'list-tab.html'
})
export class ListTab {

  dogs: Array<Dog>;

  constructor(
    public navCtrl: NavController,
    public locationTracker: LocationTracker) {
    
  }

  ionViewDidLoad() {
    this.locationTracker.startTracking();

    this.dogs = new Array();
    let hyumiko: Dog = new Dog('Hyumiko','Acting like a cat. But can bark.', 'assets/dogs/shiba.jpg');
    this.dogs.push(hyumiko);
    let reveur: Dog = new Dog('Reveur','Acting like a carpet.', 'assets/dogs/teckel.jpg');
    this.dogs.push(reveur);
  }

}
