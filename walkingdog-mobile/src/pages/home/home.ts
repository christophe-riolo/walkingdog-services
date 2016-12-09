import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

import { MapTab } from './map-tab/map-tab';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {


  listTab: any;
  mapTab: any;

  constructor(public navCtrl: NavController) {
    this.mapTab = MapTab;
  }

}
