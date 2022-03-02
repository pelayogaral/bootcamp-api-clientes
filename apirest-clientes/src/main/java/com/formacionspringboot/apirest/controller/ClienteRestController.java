package com.formacionspringboot.apirest.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	/*@PostMapping("/cliente")//Comentario
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente saveCliente(@RequestBody Cliente cliente)
	{
		return servicio.save(cliente);
	}*/
	@PostMapping("/cliente")
	public ResponseEntity<?> saveCliente(@RequestBody Cliente cliente)
	{
		Cliente clienteNew = null;
		Map<String,Object> response = new HashMap<>();
		
		try
		{
			clienteNew = servicio.save(cliente);		
		}
		catch(DataAccessException e) 
		{
			response.put("mensaje", "Error al realizar una insert a base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ".concat(clienteNew.getNombre()).concat(" ha sido creado con éxito"));
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}
	/*@PutMapping("/cliente/{id}")
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
	}*/
	@PutMapping("/cliente/{id}")
	public ResponseEntity<?> updateCliente(@RequestBody Cliente cliente, @PathVariable Long id)
	{
		Cliente clienteActual = servicio.findById(id);	
		Map<String,Object> response = new HashMap<>();
		if(clienteActual == null)
		{
			response.put("mensaje", "No se puede editar el cliente, el ID ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
		}		
		try 
		{
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setCreatedAt(cliente.getCreatedAt());	
			
			servicio.save(clienteActual);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje", "Error al realizar un update a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ".concat(clienteActual.getNombre()).concat(" ha sido actualizado con éxito"));
		response.put("cliente", clienteActual);
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}
	/*@DeleteMapping("/cliente/{id}")
	public Cliente deleteCliente(@PathVariable Long id)
	{
		Cliente clienteEliminado = servicio.findById(id);
		servicio.delete(id);
		return clienteEliminado;
	}*/
	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<?> deleteCliente(@PathVariable Long id)
	{
		Cliente clienteEliminado = servicio.findById(id);	
		Map<String,Object> response = new HashMap<>();
		if(clienteEliminado == null)
		{
			response.put("mensaje", "No se puede eliminar el cliente, el ID ".concat(id.toString()).concat(" no existe en la base de datos"));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
		}		
		try 
		{
			String fotoAnterior = clienteEliminado.getImagen();		
			if(fotoAnterior != null && fotoAnterior.length() > 0) 
			{
				Path rutaAnterior = Paths.get("uploads").resolve(fotoAnterior).toAbsolutePath();
				File archivoAnterior = rutaAnterior.toFile();
				if(archivoAnterior.exists() && archivoAnterior.canRead())
				{
					archivoAnterior.delete();
				}
			}	
			servicio.delete(id);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje", "Error al realizar un delete a la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ".concat(clienteEliminado.getNombre()).concat(" ha sido eliminado con éxito"));
		response.put("cliente", clienteEliminado);
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	}
	
	@PostMapping("cliente/upload")
	public ResponseEntity<?> uploadImagen(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id)
	{
		Map<String,Object> response = new HashMap<>();
		
		Cliente cliente = servicio.findById(id);
		if(!archivo.isEmpty()) 
		{
			String nombreArchivo = UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();	
			try 
			{			
				Files.copy(archivo.getInputStream(), rutaArchivo);
									
			} catch (IOException e) {
				
				response.put("mensaje", "Error al subir la imagen");
				response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String fotoAnterior = cliente.getImagen();
			
			if(fotoAnterior != null && fotoAnterior.length() > 0) 
			{
				Path rutaAnterior = Paths.get("uploads").resolve(fotoAnterior).toAbsolutePath();
				File archivoAnterior = rutaAnterior.toFile();
				if(archivoAnterior.exists() && archivoAnterior.canRead())
				{
					archivoAnterior.delete();
				}
			}		
			
			cliente.setImagen(nombreArchivo);
			servicio.save(cliente);
			response.put("mensaje", "La imagen "+ nombreArchivo +" ha sido subida con exito");
			response.put("cliente", cliente);
		}	
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}
	@GetMapping("/cliente/imagen/{nombreImagen:.+}")//Con :.+ accedemos a la extension de la imagen pasada por la cabecera
	public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen)
	{
		Path rutaImagen = Paths.get("uploads").resolve(nombreImagen).toAbsolutePath();
		//Para cargar la imagen iniciamos una variable resource a nulo
		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaImagen.toUri());
		} 
		catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
		if(!recurso.exists() && !recurso.isReadable())
		{
			throw new RuntimeException("Error no se puede cargar la imagen solicitada: "+nombreImagen);
		}
		//Con cabecera indicamos el formato que tendra la cabecera a pasar
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\" "+recurso.getFilename()+"\"");
		//En cabecera indicamos que es un tipo HttpHeaders.CONTENT_DISPOSITION
		//eso indica que recibira contenido mediante la cabecera
		//y el archivo que sera descargado es el indicado en el header
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
}
