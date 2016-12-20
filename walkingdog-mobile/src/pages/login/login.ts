import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Http, Response } from '@angular/http';
import { FormBuilder,FormGroup,Validators } from '@angular/forms';

import { HomePage } from '../home/home';
import { SecurityContextHolder } from '../../components/authentication/security-context-holder'


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

    loginForm: FormGroup;
    private apiUrl: String;

    constructor(
      private securityContextHolder: SecurityContextHolder,
      private loadingCtrl: LoadingController,
      private navCtrl: NavController,
      private http: Http,
      fb: FormBuilder) {

      if (this.securityContextHolder.isAuthenticated()) {
        this.navCtrl.setRoot(HomePage);
      }

      

      this.loginForm = fb.group({
        'email': ['', Validators.required],
        'password': ['', Validators.required]
      });

      this.apiUrl = 'https://walkingdog-services.herokuapp.com/api/authentication';

    }

    ionViewDidLoad() {
    }


    login(form: any) {
      // Recreated every time we need it to fix https://github.com/driftyco/ionic/issues/6209
      let loader = this.loadingCtrl.create({
        content: "Please wait..."
      });
      loader.present();
      if (form.valid) {
        let value = form.value;
        this.http
        .post(`${this.apiUrl}/login`, JSON.stringify(value))
        .subscribe((res: Response) => {
          loader.dismiss();
          this.securityContextHolder.setCurrentUser(res.json());
          this.navCtrl.setRoot(HomePage);
        },
        (err:Response) => {
          if (err.status == 400 && err.statusText === 'user_not_enabled') {
            loader.dismiss();
            alert('Your account has not been enabled yet. Please activate it by clicking on the link provided in e-mail');
            return false;
          } else {
            loader.dismiss();
            alert('Wrong credentials');
            return false;
          }
        });
      } else {
        loader.dismiss();
        alert('Required fields : email, password');
        return false;
      }

    }

  }
