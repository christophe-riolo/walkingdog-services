import { Component } from '@angular/core';
import { NavController,LoadingController,ActionSheetController,Platform } from 'ionic-angular';
import { FormBuilder,FormGroup,Validators } from '@angular/forms';
import { StartPage } from '../start/start';
import { Http, Response } from '@angular/http';
import { Camera, File, Transfer, FilePath } from 'ionic-native';

import { Configuration } from '../../components/configuration';

declare var cordova: any;

@Component({
	selector: 'page-signup',
	templateUrl: 'signup.html'
})
export class SignupPage {

	signupForm: FormGroup;
	lastImage: string = null;

	constructor( 
		private loadingCtrl: LoadingController,
		private navCtrl: NavController,
		private http: Http,
		private configuration: Configuration,
		public actionSheetCtrl: ActionSheetController,
		private platform: Platform,
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

	presentActionSheet() {
		let actionSheet = this.actionSheetCtrl.create({
			buttons: [
			{
				text: 'Use camera',
				icon: 'camera',
				handler: () => {
					this.takePicture(Camera.PictureSourceType.CAMERA);
				}
			},{
				text: 'Load from library',
				icon: 'folder',
				handler: () => {
					this.takePicture(Camera.PictureSourceType.PHOTOLIBRARY);
				}
			},{
				text: 'Cancel',
				icon: 'close',
				role: 'cancel',
				handler: () => {
					
				}
			}
			]
		});
		actionSheet.present();
		return false;
	}

	// https://devdactic.com/ionic-2-images/
	public takePicture(sourceType) {
		// Create options for the Camera Dialog
		var options = {
			quality: 100,
			sourceType: sourceType,
			saveToPhotoAlbum: false,
			correctOrientation: true
		};

		// Get the data of an image
		Camera.getPicture(options).then((imagePath) => {
			// Special handling for Android library
			if (this.platform.is('android') && sourceType === Camera.PictureSourceType.PHOTOLIBRARY) {
				FilePath.resolveNativePath(imagePath)
				.then(filePath => {
					var currentName = imagePath.substr(imagePath.lastIndexOf('/') + 1);
					var correctPath = filePath.substr(0, imagePath.lastIndexOf('/') + 1);
					this.copyFileToLocalDir(correctPath, currentName, this.createFileName());
				});
			} else {
				var currentName = imagePath.substr(imagePath.lastIndexOf('/') + 1);
				var correctPath = imagePath.substr(0, imagePath.lastIndexOf('/') + 1);
				this.copyFileToLocalDir(correctPath, currentName, this.createFileName());
			}
		}, (err) => {
			alert('Error while selecting image.');
		});
	}

	// Create a new name for the image
	private createFileName() {
		let d = new Date();
		return d.getTime() + ".jpg";
	}

	// Copy the image to a local folder
	private copyFileToLocalDir(namePath, currentName, newFileName) {
		alert(namePath + ' ' + currentName + ' ' + newFileName);
		File.copyFile(namePath, currentName, cordova.file.dataDirectory, newFileName).then(success => {
			this.lastImage = newFileName;
		}, error => {
			alert('Error while storing file.');
		});
	}

	// Always get the accurate path to your apps folder
	public pathForImage(img) {
		if (img === null) {
			return '';
		} else {
			return cordova.file.dataDirectory + img;
		}
	}

}
