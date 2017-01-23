import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { FormBuilder,FormGroup,Validators } from '@angular/forms';
import { StartPage } from '../start/start';
import { Http, Response } from '@angular/http';

import { Configuration } from '../../components/configuration';


@Component({
	selector: 'page-signup',
	templateUrl: 'signup.html'
})
export class SignupPage {

	signupForm: FormGroup;

	constructor( 
		private loadingCtrl: LoadingController,
		private navCtrl: NavController,
		private http: Http,
		private configuration: Configuration,
		fb: FormBuilder) {

		this.signupForm = fb.group({
			'email': ['', Validators.required],
			'password': ['', Validators.required],
			'dogName': ['', Validators.required],
			'dogGender': ['', Validators.required],
			'dogBreed': ['', Validators.required],
			'dogBirthdate': ['', Validators.required]
		});
	}

	ionViewDidLoad() {
	}

	signup(form: any) {
	  // Recreated every time we need it to fix https://github.com/driftyco/ionic/issues/6209
		let loader = this.loadingCtrl.create({
			content: "Please wait..."
		});
		loader.present();
		if (form.valid) {
			let value = form.value;
			this.http
			.post(`${this.configuration.wdAuthenticationApiUrl()}/signup`, JSON.stringify(value))
			.subscribe((res: Response) => {
				if (res.status == 201) {
					loader.dismiss();
					alert('An e-mail has been sent. Please confirm you e-mail address before log in.');
					this.navCtrl.setRoot(StartPage);
				} 

			},
			(err:Response) => {
				if (err.status == 400) {
					loader.dismiss();
					alert('E-mail address already exists. Please use another one.');
					return false;
				} else {
					loader.dismiss();
					alert('Sorry, an error occured. Please come back later');
					return false;
				}
			});
		} else {
			loader.dismiss();
			alert('Required fields : email, password, dog name, dog gender, dog breed, dog birthdate');
			return false;
		}

	}

}
