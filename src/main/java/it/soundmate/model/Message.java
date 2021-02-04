package it.soundmate.model;

import it.soundmate.bean.messagebeans.UserMessageBean;
import it.soundmate.controller.logic.MessagesController;

public class Message {

    private int messageCode;
    private int idSender;
    private UserType senderUserType;
    private int idReceiver;
    private String subject;
    private String body;
    private UserMessageBean userMessageBean;


    public Message() {
    }

    public Message(int idSender, int idReceiver, String subject, String body, UserType senderUserType) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.subject = subject;
        this.body = body;
        this.senderUserType = senderUserType;
        this.userMessageBean = this.getSender();
    }

    private UserMessageBean getSender() {
        MessagesController messagesController = new MessagesController();
        return messagesController.getSender(this.idSender);
    }

    public Message withCode(int code){
        Message newMessage = new Message();
        newMessage.setIdSender(this.idSender);
        newMessage.setIdReceiver(this.idReceiver);
        newMessage.setSubject(this.subject);
        newMessage.setBody(this.body);
        newMessage.setMessageCode(code);
        newMessage.setUserMessageBean(this.userMessageBean);
        return newMessage;
    }

    public UserType getSenderUserType() {
        return senderUserType;
    }

    public void setSenderUserType(UserType senderUserType) {
        this.senderUserType = senderUserType;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }

    public int getIdSender() {
        return idSender;
    }

    public void setIdSender(int idSender) {
        this.idSender = idSender;
    }

    public int getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(int idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserMessageBean getUserMessageBean() {
        return userMessageBean;
    }

    public void setUserMessageBean(UserMessageBean userMessageBean) {
        this.userMessageBean = userMessageBean;
    }
}
