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
  potential_likes: any;

  constructor(private jwtService: JwtService) { }

  ngOnInit() {
      this.jwtService.getProfile().subscribe(profile => {
          this.user = profile;

          this.jwtService.getLikes(this.user.userId).subscribe(likes => {
              this.likes = Object.values(likes);

              this.jwtService.getDislikes(this.user.userId).subscribe(dislikes => {
                  this.dislikes = Object.values(dislikes);
              });

              this.jwtService.explore(this.user.desiredGender, this.user.gender, this.user.desiredZipcode, this.user.desiredRent).subscribe(list => {
                  this.potential_list = Object.values(list);

                  for (var i = 0; i < this.potential_list.length; i++) {
                      if (this.likes.includes(this.potential_list[i].userId) || this.dislikes.includes(this.potential_list[i].userId)) {
                          this.potential_list.splice(i,1);
                      }
                  }

                  this.potential = this.potential_list[this.count];
              });
          });

      });
  }

  like() {
      let requestBody = this.potential.userId;
      this.jwtService.like(this.user.userId, requestBody);
      this.jwtService.getLikes(this.potential.userId).subscribe(likes => {
          this.potential_likes = Object.values(likes);
          if (this.potential_likes.includes(this.user.userId)) {
              const requestBody = {
                  user1_id: this.user.userId,
                  user2_id: this.potential.userId
              };
              this.jwtService.match(requestBody);
          }
      })

      if (this.count == this.potential_list.length) {
          alert("You are out of potential matches.");
      } else {
          this.count++;
      }
      this.potential = this.potential_list[this.count];
  }

  dislike() {
      let requestBody = this.potential.userId;
      this.jwtService.dislike(this.user.userId, requestBody);
      if (this.count == this.potential_list.length) {
          alert("You are out of potential matches.");
      } else {
          this.count++;
      }
      this.potential = this.potential_list[this.count];
  }
}
