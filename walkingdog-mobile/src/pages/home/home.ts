import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoginPage } from '../login/login';
import { Auth } from '@ionic/cloud-angular';


@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  constructor(public navCtrl: NavController, public auth: Auth) {

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
