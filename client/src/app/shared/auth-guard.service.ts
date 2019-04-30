import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { JwtService } from './jwt.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private jwtService: JwtService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (this.jwtService.loggedIn) {
      return true;
    }

    this.router.navigate(['home']);
    return false;
  }
}
