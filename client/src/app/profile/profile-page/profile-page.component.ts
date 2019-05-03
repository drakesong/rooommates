import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JwtService } from '../../shared/jwt.service';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-profile-page',
    templateUrl: './profile-page.component.html',
    styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {
    user: Object;
    hide = true;

    constructor(private jwtService: JwtService) { }

    ngOnInit() {
        this.user = this.jwtService.getProfile().subscribe(profile => {
            this.user = profile;
        });
    }

    onUpdate() {
        // const httpOptions = {
        //     headers: new HttpHeaders({
        //         'Content-Type': 'application/json'
        //     })
        // };
        //
        // let requestBody = this.user;

        // this.http.post(environment.API_BASE_URL + "edit", requestBody, httpOptions)
        //     .subscribe(response => {
        //         alert(response['message']);
        //     }, error => {
        //         alert(error.error.message);
        //     });
    }
}
