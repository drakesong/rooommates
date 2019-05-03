import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuardService } from './shared/auth-guard.service';

const routes: Routes = [
    { path: 'home', loadChildren: './home/home.module#HomeModule', data: {animation: 'Home'} },
    { path: 'chat', loadChildren: './chat/chat.module#ChatModule', data: {animation: 'Chat'} },
    { path: 'explore', loadChildren: './explore/explore.module#ExploreModule', data: {animation: 'Explore'} },
    { path: 'profile', loadChildren: './profile/profile.module#ProfileModule', data: {animation: 'Profile'}, canActivate: [AuthGuardService] },
    { path: 'register', loadChildren: './register/register.module#RegisterModule', data: {animation: 'Register'} },
    { path: 'sign-in', loadChildren: './sign-in/sign-in.module#SignInModule', data: {animation: 'SignIn'} },
    { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
