import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { JwtService } from './jwt.service';
import { Observable, of as observableOf, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {
  public getLoggedInStatus = new Subject();

  constructor(private jwtService: JwtService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (this.jwtService.loggedIn) {
      this.getLoggedInStatus.next('Sign Out');
      return true;
    }

    this.getLoggedInStatus.next('Sign In');
    this.router.navigate(['home']);
    alert("You must be logged in.");
    return false;
  }

  checkStatus(): void {
    if (this.jwtService.loggedIn) {
      this.getLoggedInStatus.next('Sign Out');
    } else {
      this.getLoggedInStatus.next('Sign In');
    }
  }
}
