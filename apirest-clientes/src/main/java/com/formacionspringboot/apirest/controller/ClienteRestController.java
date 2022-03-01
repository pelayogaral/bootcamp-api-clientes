package com.formacionspringboot.apirest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionspringboot.apirest.entity.Cliente;
import com.formacionspringboot.apirest.service.ClienteService;


@RestController
@RequestMapping("/api")//Necesario primer paso para escribirlo
public class ClienteRestController {

	@Autowired
	private ClienteService servicio;
	
	@GetMapping({"/clientes","/todos"})//Mediante las llaves podremos tener dentro varios mappings en vez de 1 solo
	public List<Cliente> index(){
		return servicio.findAll();
	}
	
	/*@GetMapping({"/clientes/{id}"})
	public Cliente findClienteById(@PathVariable Long id){
		return servicio.findById(id);
	}*/
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> findClienteById(@PathVariable Long id)
	{
		Cliente cliente = null;
		Map<String,Object> response = new HashMap<>();
		
		try
		{
			cliente = servicio.findById(id);		
		}
		catch(DataAccessException e) 
		{
			response.put("mensaje", "Error al realizar la consulta a base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente == null)
		{
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	@PostMapping("/cliente")//Comentario
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente saveCliente(@RequestBody Cliente cliente)
	{
		return servicio.save(cliente);
	}
	
	@PutMapping("/cliente/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente updateCliente(@RequestBody Cliente cliente, @PathVariable Long id) 
	{
		Cliente clienteUpdate = servicio.findById(id);
		
		clienteUpdate.setNombre(cliente.getNombre());
		clienteUpdate.setApellido(cliente.getApellido());
		clienteUpdate.setEmail(cliente.getEmail());
		clienteUpdate.setTelefono(cliente.getTelefono());
		clienteUpdate.setCreatedAt(cliente.getCreatedAt());	
		return servicio.save(clienteUpdate);
	}
	
	@DeleteMapping("/cliente/{id}")
	public Cliente deleteCliente(@PathVariable Long id)
	{
		Cliente clienteEliminado = servicio.findById(id);
		servicio.delete(id);
		return clienteEliminado;
	}
	
}
