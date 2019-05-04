import { Component, OnInit } from '@angular/core';
import { JwtService } from '../../shared/jwt.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-explore-page',
  templateUrl: './explore-page.component.html',
  styleUrls: ['./explore-page.component.css']
})
export class ExplorePageComponent implements OnInit {
  user: any;
  potential: any;

  constructor(private jwtService: JwtService) { }

  ngOnInit() {
      this.user = this.jwtService.getProfile().subscribe(profile => {
          this.user = profile;
          this.potential = this.jwtService.explore(this.user.desiredGender, this.user.gender, this.user.desiredZipcode, this.user.desiredRent).subscribe(list => {
              this.potential = list;
          });
      });


  }
}
