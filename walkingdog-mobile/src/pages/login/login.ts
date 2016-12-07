import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Auth as IonicAuth, User as IonicUser, UserDetails, IDetailedError } from '@ionic/cloud-angular';
import { GoogleAuth, User as GoogleUser } from '@ionic/cloud-angular';

import { FormBuilder,FormGroup } from '@angular/forms';

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

    segments: string;
    loader: any;
    loginForm: FormGroup;
    signupForm: FormGroup;

    constructor(
      public ionicAuth: IonicAuth, 
      public ionicUser: IonicUser, 
      public googleAuth: GoogleAuth,
      public googleUser: GoogleUser,
      public loadingCtrl: LoadingController,
      public navCtrl: NavController,
      fb: FormBuilder) {

      if (this.ionicAuth.isAuthenticated()) {
        this.navCtrl.setRoot(HomePage);
      }

      this.segments = 'login';
      this.loader = this.loadingCtrl.create({
        content: "Please wait..."
      });

      this.loginForm = fb.group({
        'email': [''],
        'password': ['']
      });

      this.signupForm = fb.group({
        'email': [''],
        'password': ['']
      });
    }

    ionViewDidLoad() {
    }

    signup(value: any) {
      this.loader.present();
      let details: UserDetails = {'email': value.email, 'password': value.password};
      this.ionicAuth.signup(details).then(() => {
        alert('Signed up')
        this.segments = 'login';
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

    login(value: any) {
      this.loader.present();
      let details: UserDetails = {'email': value.email, 'password': value.password};
      this.ionicAuth.login('basic', details).then( () =>{
        this.loader.dismiss();
        this.navCtrl.setRoot(HomePage);
      }, (err) => {
        this.loader.dismiss();
        alert(err);
      });

    }

    loginWithGoogle() {
      this.googleAuth.login().then( () =>{
        alert('Logged in with Google');
        this.navCtrl.setRoot(HomePage);
      });
    }

    loginWithFacebook() {
      this.ionicAuth.login('facebook').then( () =>{
        alert('Logged in with Facebook');
        this.navCtrl.setRoot(HomePage);
      });
    }

  }
