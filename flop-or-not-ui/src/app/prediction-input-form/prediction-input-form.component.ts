import { Component, OnInit } from '@angular/core';
import { PredictionInput } from '../predictionInput';
import { MoviePredictionService } from '../movie-prediction.service';
import { PredictionResults } from '../predictionResults';

@Component({
  selector: 'app-prediction-input-form',
  templateUrl: './prediction-input-form.component.html',
  styleUrls: ['./prediction-input-form.component.css']
})
export class PredictionInputFormComponent implements OnInit {

  input: PredictionInput = {
    director: 'Michael Bay',
    actor1: 'Harrison Ford',
    actor2: 'Tom Cruise',
    budget: '10000000'
  };

  results: PredictionResults = {
    numberRating: -1,
    wordRating: 'Flooop'
  };

  constructor(public moviePredictionService: MoviePredictionService) { }

  ngOnInit() {
  }

  getResults(input) {

    const jsonBuilder = {
      'action': 'rateMovie',
      'director': input.director,
      'budget': String(input.budget),
      'actors': `${input.actor1},${input.actor2}`
    };

    console.log(jsonBuilder);

    this.moviePredictionService.getMoviePrediction(jsonBuilder).subscribe(
      data => {
        console.log(data);
        this.results = data;

        if (!this.results.wordRating) {
          this.results.wordRating = 'ERROR';
          this.results.numberRating = -999;
        }
      },
      error => {
        console.error('Error creating item');
      });
  }

}
