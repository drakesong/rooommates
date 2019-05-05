import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MatIconModule, MatCardModule, MatListModule, MatFormFieldModule, MatDialogModule, MatInputModule } from '@angular/material';

import { ChatPageComponent } from './chat-page/chat-page.component';
import { ChatRoutingModule } from './chat-routing.module';
import { SocketService } from './shared/services/socket.service';

@NgModule({
  declarations: [ChatPageComponent],
  imports: [
    CommonModule,
    ChatRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatFormFieldModule,
    MatDialogModule,
    MatInputModule
  ],
  providers: [SocketService],
})
export class ChatModule { }
