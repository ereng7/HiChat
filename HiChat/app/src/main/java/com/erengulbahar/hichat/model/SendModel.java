package com.erengulbahar.hichat.model;

public class SendModel
{
    public String receiver;
    public String sender;
    public String topic;
    public String message;
    public String fullTime;
    public String hourTime;
    public String docId;

    public SendModel(String receiver, String sender, String topic, String message, String fullTime, String hourTime, String docId)
    {
        this.receiver = receiver;
        this.sender = sender;
        this.topic = topic;
        this.message = message;
        this.fullTime = fullTime;
        this.hourTime = hourTime;
        this.docId = docId;
    }
}