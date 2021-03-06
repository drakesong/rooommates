import { Component, OnInit, ViewChildren, ViewChild, AfterViewInit, QueryList, ElementRef } from '@angular/core';
import { SelectionModel } from '@angular/cdk/collections';
import { MatDialog, MatDialogRef, MatList, MatListItem, MatSelectionList, MatListOption } from '@angular/material';

import { Action } from '../shared/model/action';
import { Message } from '../shared/model/message';
import { User } from '../shared/model/user';
import { SocketService } from '../shared/services/socket.service';
import { JwtService } from '../../shared/jwt.service';

@Component({
  selector: 'app-chat-page',
  templateUrl: './chat-page.component.html',
  styleUrls: ['./chat-page.component.css']
})
export class ChatPageComponent implements OnInit, AfterViewInit {
  action = Action;
  user: User;
  temp_user: any;
  messages: Message[] = [];
  messageContent: string;
  ioConnection: any;
  match_list: any;
  chat_id: any;

  @ViewChild(MatList, { read: ElementRef }) matList: ElementRef;
  @ViewChild(MatSelectionList) selectionList: MatSelectionList;
  @ViewChildren(MatListItem, { read: ElementRef }) matListItems: QueryList<MatListItem>;

  constructor(private socketService: SocketService, private jwtService: JwtService) { }

  ngOnInit() {
      this.jwtService.getProfile().subscribe(profile => {
          this.temp_user = profile;
          this.user = {
              id: this.temp_user.userId,
              name: this.temp_user.firstName,
              avatar: this.temp_user.picture
          }
          this.initIoConnection();

          this.jwtService.getMatches(this.user.id).subscribe(matches => {
              this.match_list = Object.values(matches);
          });
      });

      this.selectionList.selectedOptions = new SelectionModel<MatListOption>(false);
  }

  ngAfterViewInit(): void {
    this.matListItems.changes.subscribe(elements => {
        this.scrollToBottom();
    });
  }

  private scrollToBottom(): void {
    try {
        this.matList.nativeElement.scrollTop = this.matList.nativeElement.scrollHeight;
    } catch (err) {
        console.log(err);
    }
  }

  private initIoConnection(): void {
    this.socketService.initSocket();

    this.ioConnection = this.socketService.onMessage()
      .subscribe((message: Message) => {
        this.messages.push(message);
      });
  }

  public sendMessage(message: string): void {
    if (!message) {
      return;
    }

    let requestBody = {
        "user_id": this.user.id,
        "chat_id": this.chat_id,
        "message": message
    };

    this.socketService.send({
      from: this.user,
      content: message
    }, requestBody);
    this.messageContent = null;
  }

  private select(match: any) {
      let temp_list: Message[] = [];
      this.chat_id = match.chatId;
      this.jwtService.getMessages(this.chat_id).subscribe(messagesList => {
          let temp = Object.values(messagesList);

          temp.forEach(function(obj) {
              let temp_user: User = {
                  "id": obj.userId,
                  "name": obj.firstName,
                  "avatar": obj.picture
              };
              let temp_message: Message = {
                  "from": temp_user,
                  "content": obj.message
              };

              temp_list.push(temp_message);
          });
          this.messages = temp_list;
      })

  }
}
