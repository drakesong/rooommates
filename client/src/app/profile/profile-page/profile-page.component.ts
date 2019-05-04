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
    user: any;
    hide = true;

    constructor(private jwtService: JwtService) { }

    ngOnInit() {
        this.user = this.jwtService.getProfile().subscribe(profile => {
            this.user = profile;
        });
    }

    onUpdate() {
        let requestBody = this.user;
        if (this.user.minRent > this.user.maxRent) {
            alert("Minimum Rent should be less than Maximum Rent");
        } else {
            this.jwtService.update(requestBody);
        }
    }
}
