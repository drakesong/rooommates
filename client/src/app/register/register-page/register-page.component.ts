import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-register-page',
    templateUrl: './register-page.component.html',
    styleUrls: ['./register-page.component.css']
})
export class RegisterPageComponent implements OnInit {
    user: Object = {}

    constructor(private http: HttpClient) { }

    ngOnInit() {
    }

    onRegister() {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        };

        let requestBody = this.user;
        console.log(this.user);

        this.http.post(environment.API_BASE_URL + "register", requestBody, httpOptions)
        .subscribe(response => {
            console.log(response['message']);
        }, error => {
            alert(error.error.message);
        });
    }
}
