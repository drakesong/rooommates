import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { Routes, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { JwtService } from '../shared/jwt.service';
import { AuthGuardService } from '../shared/auth-guard.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  userStatus: any;

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches)
    );

  constructor(private breakpointObserver: BreakpointObserver, public router: Router, private jwtService: JwtService, private authGuardService: AuthGuardService) { }

  ngOnInit() {
      this.authGuardService.getLoggedInStatus.subscribe(status => this.userStatus = status);
      this.authGuardService.checkStatus();
  }

  onSignInOut() {
      if (this.userStatus == "Sign Out") {
          this.signOut();
      } else {
          this.router.navigate(['sign-in']);
      }
  }

  signOut() {
      this.jwtService.logout();
      this.authGuardService.checkStatus();
      alert("You have been signed out.");
      this.router.navigate(['home']);
  }

  checkUserStatus() {
      return this.jwtService.loggedIn;
  }
}
