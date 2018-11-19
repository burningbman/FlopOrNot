import { Component, OnInit } from '@angular/core';
import { PredictionInput } from '../predictionInput';
import { MoviePredictionService } from '../movie-prediction.service';

@Component({
  selector: 'app-prediction-input-form',
  templateUrl: './prediction-input-form.component.html',
  styleUrls: ['./prediction-input-form.component.css']
})
export class PredictionInputFormComponent implements OnInit {

  input: PredictionInput = {
    director: 'Josh Paul',
    actor: 'Ryan Gosling',
    budget: 120000
  };

  constructor(public moviePredictionService: MoviePredictionService) { }

  ngOnInit() {
  }

  getResults(input) {
    this.moviePredictionService.getMoviePrediction(input);
  }

}
