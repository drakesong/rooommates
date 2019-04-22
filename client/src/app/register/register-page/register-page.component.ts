import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-register-page',
    templateUrl: './register-page.component.html',
    styleUrls: ['./register-page.component.css']
})
export class RegisterPageComponent implements OnInit {
    user: Object = {}
    hide = true;

    constructor(private http: HttpClient, private router: Router) { }

    ngOnInit() {
    }

    onRegister() {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        };

        let requestBody = this.user;

        this.http.post(environment.API_BASE_URL + "register", requestBody, httpOptions)
            .subscribe(response => {
                alert(response['message']);
                this.router.navigate(['sign-in']);
            }, error => {
                alert(error.error.message);
            });
    }
}
