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
  potential_list: any;
  potential: any;
  count: number = 0;

  constructor(private jwtService: JwtService) { }

  ngOnInit() {
      this.jwtService.getProfile().subscribe(profile => {
          this.user = profile;

          this.jwtService.explore(this.user.desiredGender, this.user.gender, this.user.desiredZipcode, this.user.desiredRent).subscribe(list => {
              this.potential_list = list;
              console.log(this.potential_list);
              this.potential = Object.values(this.potential_list)[this.count];
              console.log(this.potential);
          });
      });
  }

  like() {
      this.count++;

  }

  dislike() {
      this.count++;
  }
}
