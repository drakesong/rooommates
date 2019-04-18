import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-sign-in-page',
  templateUrl: './sign-in-page.component.html',
  styleUrls: ['./sign-in-page.component.css']
})
export class SignInPageComponent implements OnInit {
    user: Object = {}

    constructor(private http: HttpClient) { }

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
                console.log(response['message']);
            }, error => {
                alert(error.error.message);
            });
    }
}
