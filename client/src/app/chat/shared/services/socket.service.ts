import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Observer } from 'rxjs/Observer';
import { Message } from '../model/message';

import * as socketIo from 'socket.io-client';
import { environment } from '../../../../environments/environment';
import { JwtService } from '../../../shared/jwt.service';

@Injectable()
export class SocketService {
    private socket;

    constructor(private jwtService: JwtService) { }

    public initSocket(): void {
        this.socket = socketIo(environment.CHAT_URL);
    }

    public send(message: Message, requestBody: Object): void {
        this.jwtService.message(requestBody);
        this.socket.emit('message', message);
    }

    public join(message: Message): void {
        this.socket.emit('join', message);
    }

    public onMessage(): Observable<Message> {
        return new Observable<Message>(observer => {
            this.socket.on('message', (data: Message) => observer.next(data));
        });
    }
}
