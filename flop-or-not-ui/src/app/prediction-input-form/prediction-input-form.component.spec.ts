import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictionInputFormComponent } from './prediction-input-form.component';

describe('PredictionInputFormComponent', () => {
  let component: PredictionInputFormComponent;
  let fixture: ComponentFixture<PredictionInputFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PredictionInputFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PredictionInputFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
