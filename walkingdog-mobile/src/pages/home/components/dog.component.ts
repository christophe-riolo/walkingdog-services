import { Component } from '@angular/core';

@Component({
  selector: 'dog-item',
  templateUrl: 'dog.component.html',
  inputs: ['dog']
})
export class DogComponent {

  dog: Dog;

  constructor() {
  }


}

export class Dog {
  constructor(
    public name: string, 
    public description: string, 
    public imageUrl: string) {
  }

}