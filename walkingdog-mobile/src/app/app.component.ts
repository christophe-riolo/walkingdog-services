import { Component,ViewChild } from '@angular/core';
import { NavController,Platform } from 'ionic-angular';
import { StatusBar } from 'ionic-native';

import { StartPage } from '../pages/start/start';
import { HomePage } from '../pages/home/home';
import { AboutPage } from '../pages/about/about';
import { ProfilePage } from '../pages/profile/profile';

import { Auth } from '@ionic/cloud-angular';


@Component({
  template: `
  <ion-menu [content]="content" *ngIf="auth.isAuthenticated()">
    <ion-header>
      <ion-toolbar color="secondary">
        <ion-title>Menu</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-list>
        <button menuClose ion-item icon-left (click)="openPage(homePage)">
          <ion-icon name="paw"></ion-icon>
          Dogs around
        </button>
        <button menuClose ion-item icon-left (click)="openPage(profilePage)">
          <ion-icon name="person"></ion-icon>
          Profile
        </button>
        <a href="mailto:?subject=Checkout Walking Dog app&body=to change" ion-item icon-left>
          <ion-icon name="share"></ion-icon>
          Share app
        </a>
        <button menuClose ion-item icon-left (click)="openPage(aboutPage)">
          <ion-icon name="information-circle"></ion-icon>
          About & feedback
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
  profilePage: any;

  constructor(platform: Platform, public auth: Auth) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
      this.rootPage = StartPage;
      this.homePage = HomePage;
      this.aboutPage = AboutPage;
      this.profilePage = ProfilePage;
    });
  }

  openPage(page) {
    this.navCtrl.setRoot(page);
  }

}
