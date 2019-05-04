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
  likes: any;
  dislikes: any;

  constructor(private jwtService: JwtService) { }

  ngOnInit() {
      this.jwtService.getProfile().subscribe(profile => {
          this.user = profile;
          console.log(this.user);

          this.jwtService.getLikes(this.user.userId).subscribe(likes => {
              this.likes = Object.values(likes);
              console.log(this.likes);

              this.jwtService.getDislikes(this.user.userId).subscribe(dislikes => {
                  this.dislikes = Object.values(dislikes);
                  console.log(this.dislikes);
              });

              this.jwtService.explore(this.user.desiredGender, this.user.gender, this.user.desiredZipcode, this.user.desiredRent).subscribe(list => {
                  this.potential_list = Object.values(list);
                  console.log(this.potential_list);

                  for (var i = 0; i < this.potential_list.length; i++) {
                      if (this.likes.includes(this.potential_list[i].userId) || this.dislikes.includes(this.potential_list[i].userId)) {
                          this.potential_list.splice(i,1);
                      }
                  }

                  this.potential = this.potential_list[this.count];
                  console.log(this.potential);
              });
          });

      });
  }

  like() {
      let requestBody = this.potential.userId;
      this.jwtService.like(this.user.userId, requestBody);
      this.count++;
      this.potential = this.potential_list[this.count];
  }

  dislike() {
      let requestBody = this.potential.userId;
      this.jwtService.dislike(this.user.userId, requestBody);
      this.count++;
      this.potential = this.potential_list[this.count];
  }
}
