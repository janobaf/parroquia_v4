package com.clases.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.clases.springboot.app.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {
	
	@Query(value="SELECT * FROM cliente where(dni= :dni or :dni = 'DNI')  ",nativeQuery=true)	
	public List<Cliente> buscarPorDNI(@Param("dni") String DNI);
	
	public List<Cliente> findByDni(String dni);

	@Query(value="SELECT c FROM Cliente c WHERE c.nombre is not null ORDER BY c.nombre ASC")	
	public List<Cliente> findAll();
}
