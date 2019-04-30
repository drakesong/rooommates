import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { JwtService } from '../../shared/jwt.service';

@Component({
    selector: 'app-register-page',
    templateUrl: './register-page.component.html',
    styleUrls: ['./register-page.component.css']
})
export class RegisterPageComponent implements OnInit {
    user: Object = {}
    hide = true;

    constructor(private jwtService: JwtService) { }

    ngOnInit() {
    }

    onRegister() {
        let requestBody = this.user;
        this.jwtService.register(requestBody);
    }
}
