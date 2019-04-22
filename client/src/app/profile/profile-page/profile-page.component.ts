import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-profile-page',
    templateUrl: './profile-page.component.html',
    styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {
    user: Object = {}

    constructor(private http: HttpClient) { }

    ngOnInit() {
    }

    onUpdate() {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            })
        };

        let requestBody = this.user;

        this.http.post(environment.API_BASE_URL + "edit", requestBody, httpOptions)
            .subscribe(response => {
                console.log(response['message']);
            }, error => {
                alert(error.error.message);
            });
    }
}
