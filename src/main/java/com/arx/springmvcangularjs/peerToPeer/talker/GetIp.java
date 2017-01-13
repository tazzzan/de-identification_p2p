package com.arx.springmvcangularjs.peerToPeer.talker;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

public class GetIp {
	public Message processInput(Message message){
		String ip = message.getHeaders().get("ip_address").toString();
		System.out.println("ip: " +message.getHeaders().get("ip_address"));
		
		Message mess = new GenericMessage(message.getPayload().toString() + " " + ip); 
		return mess;
	}
}
