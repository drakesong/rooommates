import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  constructor(private httpClient: HttpClient, private router: Router) { }

  public get loggedIn(): boolean{
      return localStorage.getItem('access_token') !==  null;
  }

  login(requestBody: Object) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      };
      return this.httpClient.post<{access_token: string}>(environment.API_BASE_URL + "login", requestBody, httpOptions).subscribe(response => {
        localStorage.setItem('access_token', response.access_token);
        this.router.navigate(['profile']);
      }, error => {
        alert(error.error.message);
      });
  }

  register(requestBody: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.httpClient.post(environment.API_BASE_URL + "register", requestBody, httpOptions).subscribe(response => {
      this.router.navigate(['sign-in']);
    }, error => {
      alert(error.error.message);
    });
  }

  logout() {
    localStorage.removeItem('access_token');
  }

  getProfile() {
      const httpOptions = {
          headers: new HttpHeaders({
              'Content-Type': 'application/json'
          }),
          params: new HttpParams().append('email', localStorage.getItem('access_token'))
      };

      return this.httpClient.get(environment.API_BASE_URL + "profile", httpOptions).pipe(tap(response => {
          return response;
      }, error => {
          alert(error.error.message);
      }));
  }

  update(requestBody: Object) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      };
      return this.httpClient.post(environment.API_BASE_URL + "edit", requestBody, httpOptions).subscribe(response => {
          alert(response['message']);
      }, error => {
        alert(error.error.message);
      });
  }
}
