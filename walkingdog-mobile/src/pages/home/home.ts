import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoginPage } from '../login/login';
import { Auth } from '@ionic/cloud-angular';

import { Dog } from './components/dog.component';

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  dogs: Array<Dog>;

  constructor(public navCtrl: NavController, public auth: Auth) {
    this.dogs = new Array();
    let hyumiko: Dog = new Dog('Hyumiko','Acting like a cat. But can bark.', 'assets/dogs/shiba.jpg');
    this.dogs.push(hyumiko);
    let reveur: Dog = new Dog('Reveur','Acting like a carpet.', 'assets/dogs/teckel.jpg');
    this.dogs.push(reveur);
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
