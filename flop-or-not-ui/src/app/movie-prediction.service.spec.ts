import { TestBed } from '@angular/core/testing';

import { MoviePredictionService } from './movie-prediction.service';

describe('MoviePredictionService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MoviePredictionService = TestBed.get(MoviePredictionService);
    expect(service).toBeTruthy();
  });
});
