package com.arx.springmvcangularjs.peerToPeer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.MapPropertySource;

import com.arx.springmvcangularjs.beans.dataset.DatasetP2P;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.property.Properties;
import com.arx.springmvcangularjs.handler.DatasetProvider;
import com.arx.springmvcangularjs.peerToPeer.talker.SimpleGateway;
import com.arx.springmvcangularjs.peerToPeer.talker.SimpleGateway2;
import com.arx.springmvcangularjs.peerToPeer.talker.SimpleGateway3;


public class PeersManager {
	
	List<Peer> peersList = new ArrayList<Peer>();
	
	
	public PeersManager() {
		
	}
	/** Acting as client-side
	 */
	public Peer addNewPeer(String localPeerId, String remoteIp) {
		Peer remotePeer = new Peer();
		remotePeer.setIp(remoteIp);
		createNewRemotePeerConnection(remotePeer);
		remotePeer.setId(checkPeersId(remotePeer, localPeerId));
		peersList.add(remotePeer);	
		
		return remotePeer;
	}
	
	/** (Re-)Acting as server-side
	 */
	public void addPeerClient(String remotePeerId, String ip){
    	/** If peer does not exist 
    	 *  Save and establish new connection
    	 */ 
		if(getPeer(remotePeerId) == null){
    		Peer newPeer = new Peer(remotePeerId);
    		newPeer.setIp(ip);
    		createNewRemotePeerConnection(newPeer);
	    	peersList.add(newPeer);
    	}
	}
	
	public Peer getPeer(String peerId){
		for(Peer peer : peersList){
			if(peer.getId().equals(peerId)){
				return peer;
			}
		}
		return null;
	}
	
	public List<String> getAllPeersId() {
	    List<String> returnPeersList = new ArrayList<String>(); 
	    for(Peer peer: peersList){
	    	returnPeersList.add(peer.getId());
	    }
	    return returnPeersList;
	}

	public void checkForSimilarDatasetsInNetwork(DatasetServer dataset, DatasetProvider dp){
		for(Peer peer : peersList){
			String[][] similarDatasets = sendPropetiesToPeers(peer.getId(), dataset.getProperties());
			
			/** Save similar datasets in dataset 
			 */
			for(int i=0; i<similarDatasets.length; i++){
				if(dataset.getProperties().getId().equals(similarDatasets[i][0])){
					DatasetP2P datasetInNetwork = new DatasetP2P(similarDatasets[i][1]);
					datasetInNetwork.getProperties().setPeerId(peer.getId());
					
					/** Add similar datasets from network to result_server and result_client
					 */
					dataset.getSimilarDatasets().add(datasetInNetwork);
					dp.deliverDatasetClient(dataset.getProperties().getId()).getSimilarDatasets().add(datasetInNetwork);
				}
			}
			
		}
	}
	
	/**Creates a connection to a remote peer
	 * -> acts as client who is sending requests
	 * -> Peer_ID and IP needed
	 */	
	public void createNewRemotePeerConnection(Peer peer){
		peer.setContext(setupContextClient(peer.getIp()));
	}
	
	/** Creates a local Peer connection setup 
	 * -> acts as server who is waiting for inbound connections
	 */
	public void createNewLocalPeerConnection(Peer peer){
		peer.setContext(setupContextServer());
	}
	
	
	public String checkPeersId(Peer remotePeer, String localPeerId){
		final SimpleGateway3 gateway5602 = remotePeer.getContext().getBean(SimpleGateway3.class);
		return gateway5602.send(localPeerId);
	}


	public String[][] sendPropetiesToPeers(String peerId, Properties properties) {
		Peer peer = getPeer(peerId);	
		final SimpleGateway2 gateway5601 = peer.getContext().getBean(SimpleGateway2.class);	
		return gateway5601.send(properties);
		
	}
	
	public String[][] sendParametersToPeers(String peerId, ParameterClient parameters){
		Peer peer = getPeer(peerId);		
		final SimpleGateway gateway5600 = peer.getContext().getBean(SimpleGateway.class);	
	    return gateway5600.send(parameters);
	}

	/**Enabling Ports on sockets for incomming messages from remote Peers 
	 * TO CLIENT | Port needed
	 */
	public static GenericXmlApplicationContext setupContextServer() {
		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.load("classpath:META-INF/spring/integration/viaTcpToController.xml");
		context.registerShutdownHook();
		context.refresh();
		return context;
	}
	

	/**Creating a new connection acting as client-side 
	 * TO SERVER | IP:Port needed
	 */
	public static GenericXmlApplicationContext setupContextClient(String ip) {
		final GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		
		final Map<String, Object> ips = new HashMap<String, Object>();
		ips.put("remoteIp", ip);
		final MapPropertySource propertySource = new MapPropertySource("ips", ips);
		context.getEnvironment().getPropertySources().addLast(propertySource);
		
		context.load("classpath:META-INF/spring/integration/toTcpFromClient.xml");
		context.registerShutdownHook();
		context.refresh();
		return context;
	}

	/**
	 * Getters and Setters
	 */
	
	public List<Peer> getPeersList() {
		return peersList;
	}

	public void setPeersList(List<Peer> peersList) {
		this.peersList = peersList;
	}
}
