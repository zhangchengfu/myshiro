package com.laozhang.myshiro.service;

import java.util.List;

import com.laozhang.myshiro.entity.Client;

public interface ClientService {

	Client creatClient(Client client);
	
	Client updateClient(Client client);
	
	void deleteClient(Long clientId);
	
	Client findOne(Long clientId);
	
	List<Client> findAll();
	
	Client findByClientId(String clientId);
	
	Client findByClientSecret(String clientSecret);
}
