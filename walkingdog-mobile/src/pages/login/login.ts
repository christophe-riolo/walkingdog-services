import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Auth, User, UserDetails, IDetailedError } from '@ionic/cloud-angular';

import { HomePage } from '../home/home';


/*
  Generated class for the Login page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
  */
  @Component({
      selector: 'page-login',
      templateUrl: 'login.html'
  })
  export class LoginPage {

      loginSegment: string;
      loader: any;

      constructor(
          public auth: Auth, 
          public user: User, 
          public loadingCtrl: LoadingController,
          public navCtrl: NavController) {

          if (this.auth.isAuthenticated()) {
              this.navCtrl.setRoot(HomePage);
          }

          this.loginSegment = 'login';
          this.loader = this.loadingCtrl.create({
              content: "Please wait..."
          });
      }

      ionViewDidLoad() {
          console.log('Hello Login Page');
      }

      signup(email: HTMLInputElement, password: HTMLInputElement) {
          this.loader.present();
          console.log(`Submitted values : email = ${email.value}, password = ${password.value}`);
          let details: UserDetails = {'email': email.value, 'password': password.value};
          this.auth.signup(details).then(() => {
              alert('Signed up')
              this.loader.dismiss();
          }, (err: IDetailedError<string[]>) => {
              for (let e of err.details) {
                  if (e === 'conflict_email') {
                      alert('Email already exists.');
                  } else {
                      alert(e);
                  }
              }
              this.loader.dismiss();
          });
      }

      login(email: HTMLInputElement, password: HTMLInputElement) {
          this.loader.present();
          console.log(`Submitted values : email = ${email.value}, password = ${password.value}`);
          let details: UserDetails = {'email': email.value, 'password': password.value};
          this.auth.login('basic', details).then( () =>{
              this.loader.dismiss();
              this.navCtrl.setRoot(HomePage);
          });

      }

      loginWithGoogle() {
          this.auth.login('google').then( () =>{
              alert('Logged in with Google');
              this.navCtrl.setRoot(HomePage);
          });
      }

  }
