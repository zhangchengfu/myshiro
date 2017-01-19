package com.laozhang.myshiro.dao;

import java.util.List;

import com.laozhang.myshiro.entity.Client;

public interface ClientDao {

	Client createClient(Client client);
	
	Client updateClient(Client client);
	
	void deleteClient(Long clientId);
	
	Client findOne(Long clientId);
	
	List<Client> findAll();
	
	Client findByClientId(String clientId);
	
	Client findByClientSecret(String clientSecret);
}
