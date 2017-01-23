import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Auth as IonicAuth } from '@ionic/cloud-angular';
import { GoogleAuth, User as GoogleUser } from '@ionic/cloud-angular';

import { FormBuilder,FormGroup } from '@angular/forms';

import { LoginPage } from '../login/login'
import { SignupPage } from '../signup/signup'
import { HomePage } from '../home/home';
import { SecurityContextHolder } from '../../components/authentication/security-context-holder';



/*
  Generated class for the Start page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
  */
  @Component({
    selector: 'page-start',
    templateUrl: 'start.html'
  })
  export class StartPage {

    //segments: string;
    loader: any;
    loginForm: FormGroup;
    //signupForm: FormGroup;

    constructor(
      private securityContextHolder: SecurityContextHolder,
      private ionicAuth: IonicAuth, 
      private googleAuth: GoogleAuth,
      private googleUser: GoogleUser,
      private loadingCtrl: LoadingController,
      private navCtrl: NavController,
      fb: FormBuilder) {

      if (this.securityContextHolder.isAuthenticated()) {
        this.navCtrl.setRoot(HomePage);
      }

      this.loader = this.loadingCtrl.create({
        content: "Please wait..."
      });

    }

    ionViewDidLoad() {
    }

    buttonLoginWithGoogle() {
      this.googleAuth.login().then( () =>{
        alert('Logged in with Google');
        this.navCtrl.setRoot(HomePage);
      });
    }

    buttonLoginWithFacebook() {
      this.ionicAuth.login('facebook').then( () =>{
        alert('Logged in with Facebook');
        this.navCtrl.setRoot(HomePage);
      });
    }

    buttonLogin() {
      this.navCtrl.push(LoginPage);
    }

    buttonSignup() {
      this.navCtrl.push(SignupPage);
    }
  }
