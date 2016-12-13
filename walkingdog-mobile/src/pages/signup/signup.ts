import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { Auth as IonicAuth, User as IonicUser, UserDetails, IDetailedError } from '@ionic/cloud-angular';
import { FormBuilder,FormGroup,Validators } from '@angular/forms';
import { StartPage } from '../start/start';
import { Http, Response } from '@angular/http';


@Component({
	selector: 'page-signup',
	templateUrl: 'signup.html'
})
export class SignupPage {

	loader: any;
	signupForm: FormGroup;
	private apiUrl: String;

	constructor(
		private ionicAuth: IonicAuth, 
		private ionicUser: IonicUser, 
		private loadingCtrl: LoadingController,
		private navCtrl: NavController,
		private http: Http,
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

		this.apiUrl = 'https://walkingdog-services.herokuapp.com/api/authentication';


	}

	ionViewDidLoad() {
	}

	signup(form: any) {
		this.loader.present();
		if (form.valid) {
			let value = form.value;
			// Sends location of current user to server, to be used by others
			this.http.post(
				`${this.apiUrl}/signup`,
				value)
			.subscribe((res: Response) => {
				if (res.status == 201) {
					alert('You are now signed up ! You will be redirect to login page');
					this.navCtrl.setRoot(StartPage);
				}
				else {
					alert('Sorry, an error occured. Please come back later');
				}
				this.loader.dismiss();
			});
		} else {
			this.loader.dismiss();
			alert('Required fields : email, password, dog name, dog gender, dog breed');
		}

	}

}
