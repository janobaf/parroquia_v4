package com.clases.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.clases.springboot.app.models.entity.LibroDetalle;
import com.clases.springboot.app.models.entity.Tipo;

public interface ILibroDetalleDao extends CrudRepository<LibroDetalle,Long>{

	/*Buscar Libro Bautizo*/	
	@Query(value="SELECT LD.id, C.dni, LD.num_registro, LD.num_registro, LD.libro, LD.num_pag, LD.parroco, LD.padrino, LD.madrina, LD.bautizo FROM libro_detalle LD " + 
			"JOIN cliente C " + 
			"ON LD.cliente = C.id " +
			"WHERE (dni:dni or :dni = 'dni') ",nativeQuery=true)	
	public List<LibroDetalle> buscarPorLibroDetalleDni(String dni);
	

	public List<LibroDetalle> findByIdLibroTipoSacramento(Tipo tipoSacramento);

	
	@Query(value="Select ld FROM LibroDetalle ld where YEAR(ld.fechaSacramento)=:anio and (MONTH(ld.fechaSacramento)=:mes or :mes=isnull(ld.fechaSacramento,13))")
	public List<LibroDetalle> buscarPorAnioAndMes(int anio, int mes);

	
	
	
}
