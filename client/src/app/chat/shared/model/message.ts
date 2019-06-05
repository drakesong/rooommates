import { User } from './user';
import { Action } from './action';

export interface Message {
    from?: User;
    chatId?: string;
    content?: any;
    action?: Action;
}
