import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Auth as IonicAuth, User as IonicUser, UserDetails, IDetailedError } from '@ionic/cloud-angular';
import { FormBuilder,FormGroup,Validators } from '@angular/forms';
import { StartPage } from '../start/start';

@Component({
	selector: 'page-signup',
	templateUrl: 'signup.html'
})
export class SignupPage {

	loader: any;
	signupForm: FormGroup;

	constructor(
		private ionicAuth: IonicAuth, 
		private ionicUser: IonicUser, 
		private loadingCtrl: LoadingController,
		private navCtrl: NavController,
		fb: FormBuilder) {

		this.loader = this.loadingCtrl.create({
			content: "Please wait..."
		});

		this.signupForm = fb.group({
			'email': ['', Validators.required],
			'password': ['', Validators.required],
			'dogName': ['', Validators.required],
			'dogGender': ['', Validators.required],
			'dogBreed': ['', Validators.required],
			'dogBirthdate': ['']
		});

	}

	ionViewDidLoad() {
	}

	signup(form: any) {
		this.loader.present();
		if (form.valid) {
			let value = form.value;
			console.log(value);
			let details: UserDetails = {'email': value.email, 'password': value.password};
			this.ionicAuth.signup(details).then(() => {
        this.loader.dismiss();
				alert('A confirmation e-mail has been sent !');
        this.navCtrl.setRoot(StartPage);
			}, (err: IDetailedError<string[]>) => {
				for (let e of err.details) {
					if (e === 'conflict_email') {
						alert('Email already exists.');
					} else {
						alert('Sorry, an unexpected error occured. Please contact the developer.');
					}
				}
				this.loader.dismiss();
			});
		} else {
      this.loader.dismiss();
			alert('Required fields : email, password, dog name, dog gender, dog breed');
		}

	}

}
