import { Component, OnInit, Input } from '@angular/core';
import { PredictionResults } from '../predictionResults';

@Component({
  selector: 'app-prediction-results',
  templateUrl: './prediction-results.component.html',
  styleUrls: ['./prediction-results.component.css']
})
export class PredictionResultsComponent implements OnInit {

  @Input() results: PredictionResults;

  constructor() { }

  ngOnInit() {
  }

}
