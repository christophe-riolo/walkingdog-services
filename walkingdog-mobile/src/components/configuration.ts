import { Injectable } from '@angular/core';

@Injectable()
export class Configuration {

  constructor() {
  }

  public wdLocationApiUrl() : string {
    return 'https://walkingdog-services.herokuapp.com/api/location';
  }

  public wdAuthenticationApiUrl() : string {
    return 'https://walkingdog-services.herokuapp.com/api/authentication';
  }

}