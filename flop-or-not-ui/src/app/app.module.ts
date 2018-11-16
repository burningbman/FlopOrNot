import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PredictionInputFormComponent } from './prediction-input-form/prediction-input-form.component';
import { PredictionResultsComponent } from './prediction-results/prediction-results.component';

@NgModule({
  declarations: [
    AppComponent,
    PredictionInputFormComponent,
    PredictionResultsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
