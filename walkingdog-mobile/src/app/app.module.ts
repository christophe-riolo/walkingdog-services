// Core
import { NgModule } from '@angular/core';
import { IonicApp, IonicModule } from 'ionic-angular';
import { MyApp } from './app.component';
// Pages
import { StartPage } from '../pages/start/start';
import { LoginPage } from '../pages/login/login';
import { SignupPage } from '../pages/signup/signup';
import { HomePage } from '../pages/home/home';
import { MapTab } from '../pages/home/map-tab/map-tab';
import { AboutPage } from '../pages/about/about';
import { ProfilePage } from '../pages/profile/profile';
// Components
import { LocationTracker } from '../components/location/location-tracker';
import { TimerComponent } from '../components/timer/timer';
import { DogComponent } from '../components/dog/dog.component';
// Third parties
import { CloudSettings, CloudModule } from '@ionic/cloud-angular';

const cloudSettings: CloudSettings = {
  'core': {
    'app_id': '66a0a8f6'
  },
  'auth': {
    'google': {
      'webClientId': '112751394957-bji8or17opkmn66ukqsm00q7312p1ml8.apps.googleusercontent.com',
      'scope': ['email', 'public_profile']
    }
  }
};

@NgModule({
  declarations: [
    MyApp,
    StartPage,
    LoginPage,
    SignupPage,
    HomePage,
    MapTab,
    AboutPage,
    ProfilePage,
    DogComponent,
    TimerComponent
  ],
  imports: [
    IonicModule.forRoot(MyApp),
    CloudModule.forRoot(cloudSettings)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    StartPage,
    LoginPage,
    SignupPage,
    HomePage,
    MapTab,
    AboutPage,
    ProfilePage
  ],
  providers: [
    LocationTracker
  ]
})
export class AppModule {}
