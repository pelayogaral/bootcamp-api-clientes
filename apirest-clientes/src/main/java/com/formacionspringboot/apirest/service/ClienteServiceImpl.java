package com.formacionspringboot.apirest.service;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionspringboot.apirest.dao.ClienteDAO;
import com.formacionspringboot.apirest.entity.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService{

	@Autowired
	private ClienteDAO clienteDAO;
	@Override
	@Transactional(readOnly=true) 
	//Transactional nos ayuda a toda la parte de inyección de repositorios y servicios
	public List<Cliente> findAll() 
	{	
		return (List<Cliente>) clienteDAO.findAll();
	}
	
	@Override
	@Transactional(readOnly=true)
	public Cliente findById(Long id) 
	{
		return clienteDAO.findById(id).orElse(null); //El else es por si no nos devuelve un cliente que devuelva null
	}

	@Override
	@Transactional
	public Cliente save(Cliente cliente) 
	{	
		return clienteDAO.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDAO.deleteById(id);

	}

}
