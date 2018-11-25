import { Injectable } from '@angular/core';
import { PredictionResults } from './predictionResults';

// import { Observable } from 'rxjs/Rx';
// import { Observable } from 'rxjs/Observable';
import {Http, Response, RequestOptions, Headers} from '@angular/http';
// import 'rxjs/add/operator/map';
// import 'rxjs/add/operator/do';
import { map } from 'rxjs/operators';
// import 'rxjs/add/operator/filter';

@Injectable({
  providedIn: 'root'
})
export class MoviePredictionService {

  private API_URL = 'https://nm7ulbztb8.execute-api.us-east-1.amazonaws.com/default/searchForPersons/';

  // results: PredictionResults = {IMDBScore: -1};
  results: PredictionResults = {
    numberRating: -1,
    wordRating: 'Floop'
  };

  constructor(private http: Http) { }


  getMoviePrediction(input) { // : Observable<PredictionResults> {

    const headers = new Headers({'Content-Type' : 'application/json'});
    const options = new RequestOptions({ headers: headers});
    // let INFO =  Object.assign(lastName, firstName, email, comments, option);
    const body = JSON.stringify(input);
    return this.http.post(this.API_URL, body, options).pipe(map((res: Response) => res.json()));

  }
}
