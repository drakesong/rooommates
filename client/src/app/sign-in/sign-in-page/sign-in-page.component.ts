import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JwtService } from '../../shared/jwt.service';

@Component({
  selector: 'app-sign-in-page',
  templateUrl: './sign-in-page.component.html',
  styleUrls: ['./sign-in-page.component.css']
})
export class SignInPageComponent implements OnInit {
    user: Object = {}
    hide = true;

    constructor(private jwtService: JwtService) { }

    ngOnInit() {
    }

    onLogin() {
        let requestBody = this.user;
        this.jwtService.login(requestBody);
    }
}
