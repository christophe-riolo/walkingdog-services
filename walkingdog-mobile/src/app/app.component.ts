import { Component,ViewChild } from '@angular/core';
import { NavController,Platform } from 'ionic-angular';
import { StatusBar } from 'ionic-native';

import { LoginPage } from '../pages/login/login';
import { HomePage } from '../pages/home/home';
import { AboutPage } from '../pages/about/about';
import { ContactPage } from '../pages/contact/contact';

import { Auth } from '@ionic/cloud-angular';


@Component({
  template: `
  <ion-menu [content]="content" *ngIf="auth.isAuthenticated()">
  <ion-content>
  <ion-list>
  <button menuClose ion-item (click)="openPage(homePage)">
  Home
  </button>
  <button menuClose ion-item (click)="openPage(aboutPage)">
  About
  </button>
  <button menuClose ion-item (click)="openPage(contactPage)">
  Contact
  </button>
  </ion-list>
  </ion-content>
  </ion-menu>

  <ion-nav #rootNavController #content [root]="rootPage" swipeBackEnabled="false"></ion-nav>
  `
})
export class MyApp {
  @ViewChild('rootNavController') navCtrl : NavController;
  rootPage: any;
  homePage: any;
  aboutPage: any;
  contactPage: any;

  constructor(platform: Platform, public auth: Auth) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
      this.rootPage = LoginPage;
      this.homePage = HomePage;
      this.aboutPage = AboutPage;
      this.contactPage = ContactPage;
    });
  }

  openPage(page) {
    this.navCtrl.setRoot(page);
  }

}
