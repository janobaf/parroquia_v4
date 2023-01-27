package com.clases.springboot.app.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.clases.springboot.app.models.entity.Empleado;
import com.clases.springboot.app.models.entity.Report;
import com.itextpdf.text.DocumentException;

import net.sf.jasperreports.engine.JRException;

public interface IEmpleadoService {

	public List<Empleado> findAll();
	
	public Report reporteEmpleado()throws Exception,JRException, IOException,  ParseException, DocumentException;

	Empleado findById(Long id);

	void save(Empleado empleado);

	void deleteById(Long id);
	
	public List<Empleado> buscarPorDNI(String DNI);
	
	public List<Empleado> findByCargoIdId(Long cargoId);
}
