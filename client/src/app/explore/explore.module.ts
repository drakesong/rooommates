import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule, MatSliderModule, MatIconModule, MatCardModule } from '@angular/material';

import { ExploreRoutingModule } from './explore-routing.module';
import { ExplorePageComponent } from './explore-page/explore-page.component';

@NgModule({
  declarations: [ExplorePageComponent],
  imports: [
    CommonModule,
    ExploreRoutingModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSliderModule,
    MatIconModule,
    MatCardModule
  ]
})
export class ExploreModule { }
