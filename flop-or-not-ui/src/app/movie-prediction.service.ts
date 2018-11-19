import { Injectable } from '@angular/core';
import { PredictionResults } from './predictionResults';

@Injectable({
  providedIn: 'root'
})
export class MoviePredictionService {

  results: PredictionResults = {IMDBScore: -1};

  constructor() { }

  getMoviePrediction(input) {
    const max = 999;
    const min = 1;
    this.results.IMDBScore = Math.floor(Math.random() * (max - min + 1)) + min;
  }
}
