import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-sign-in-page',
  templateUrl: './sign-in-page.component.html',
  styleUrls: ['./sign-in-page.component.css']
})
export class SignInPageComponent implements OnInit {
    user: Object = {}
    hide = true;

    constructor(private http: HttpClient, private router: Router) { }

    ngOnInit() {
    }

    onLogin() {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        };

        let requestBody = this.user;

        this.http.post(environment.API_BASE_URL + "login", requestBody, httpOptions)
            .subscribe(response => {
                alert(response['message']);
                this.router.navigate(['profile']);
            }, error => {
                alert(error.error.message);
            });
    }
}
