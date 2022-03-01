package com.formacionspringboot.apirest.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.formacionspringboot.apirest.entity.Cliente;
@Repository
public interface ClienteDAO extends CrudRepository<Cliente, Long> {

}
