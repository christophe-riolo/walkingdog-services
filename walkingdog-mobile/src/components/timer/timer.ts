import { Component } from '@angular/core';
import { ToastController } from 'ionic-angular';
import { LocationTracker } from '../location/location-tracker';


@Component({
  selector: 'timer',
  templateUrl: 'timer.html',
  inputs: ['timeInSeconds']
})
export class TimerComponent {

  timeInSeconds: number;
  private timer: CountdownTimer;
  private startWalkToast: any;
  private stopWalkToast: any;

  constructor(
    public toastCtrl: ToastController,
    public locationTracker: LocationTracker) {}

  ngOnInit() {
    this.initTimer();
    this.startWalkToast = this.toastCtrl.create({
      message: 'You are now out for a walk. Other walkers are now able to follow you and ping you.',
      duration: 8000,
      position: 'top'
    });
    this.stopWalkToast = this.toastCtrl.create({
      message: "You finished your walk. Other walkers won't be able to follow you or ping you.",
      duration: 8000,
      position: 'top'
    });
  }

  startWalk() {
    this.startWalkToast.present();
    this.locationTracker.startTracking();
    this.startTimer();
  }

  cancelWalk() {
    this.stopWalkToast.present();
    this.locationTracker.stopTracking();
    this.stopTimer();
  }

  /*
  private hasFinished() {
    return this.timer.hasFinished;
  }
  */

  private initTimer() {
    if(!this.timeInSeconds) { this.timeInSeconds = 0; }
    this.timer = <CountdownTimer>{
      seconds: this.timeInSeconds,
      runTimer: false,
      hasStarted: false,
      hasFinished: false,
      secondsRemaining: this.timeInSeconds
    };
    this.timer.displayTime = this.getSecondsAsDigitalClock(this.timer.secondsRemaining);
  }

  private startTimer() {
    this.timer.hasStarted = true;
    this.timer.runTimer = true;
    this.timerTick();
  }

  private pauseTimer() {
    this.timer.runTimer = false;
  }

  /*
  private resumeTimer() {
    this.startTimer();
  }
  */

  private stopTimer() {
    this.pauseTimer();
    this.initTimer();
  }

  private timerTick() {
    setTimeout(() => {
      if (!this.timer.runTimer) { return; }
      this.timer.secondsRemaining--;
      this.timer.displayTime = this.getSecondsAsDigitalClock(this.timer.secondsRemaining);
      if (this.timer.secondsRemaining > 0) {
        this.timerTick();
      }
      else {
        this.timer.hasFinished = true;
      }
    }, 1000);
  }

  private getSecondsAsDigitalClock(inputSeconds: number) {
    var sec_num = parseInt(inputSeconds.toString(), 10); // don't forget the second param
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);
    var hoursString = '';
    var minutesString = '';
    var secondsString = '';
    hoursString = (hours < 10) ? "0" + hours : hours.toString();
    minutesString = (minutes < 10) ? "0" + minutes : minutes.toString();
    secondsString = (seconds < 10) ? "0" + seconds : seconds.toString();
    return hoursString + ':' + minutesString + ':' + secondsString;
  }

}

export interface CountdownTimer {
  seconds: number;
  secondsRemaining: number;
  runTimer: boolean;
  hasStarted: boolean;
  hasFinished: boolean;
  displayTime: string;
}