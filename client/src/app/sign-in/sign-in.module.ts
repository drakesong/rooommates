import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatFormFieldModule, MatInputModule, MatButtonModule } from '@angular/material';

import { SignInRoutingModule } from './sign-in-routing.module';
import { SignInPageComponent } from './sign-in-page/sign-in-page.component';

@NgModule({
  declarations: [SignInPageComponent],
  imports: [
    CommonModule,
    SignInRoutingModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class SignInModule { }
