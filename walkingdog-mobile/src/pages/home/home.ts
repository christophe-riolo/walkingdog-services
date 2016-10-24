import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoginPage } from '../login/login';
import { Auth } from '@ionic/cloud-angular';

import { ListTab } from './list-tab/list-tab';
import { MapTab } from './map-tab/map-tab';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {


  listTab: any;
  mapTab: any;

  constructor(public navCtrl: NavController, public auth: Auth) {
    this.listTab = ListTab;
    this.mapTab = MapTab;
  }

  logout() {
    console.log(`User logged out`)
    this.auth.logout();
    this.navCtrl.setRoot(LoginPage).then(data => {
      console.log(`Data is ${data}`);
    }, (error) => {
      console.log(`Error is ${error}`);
    });
  }

}
