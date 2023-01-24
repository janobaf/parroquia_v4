package com.clases.springboot.app.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.clases.springboot.app.models.entity.Cliente;
import com.clases.springboot.app.models.entity.Report;
import com.itextpdf.text.DocumentException;

import net.sf.jasperreports.engine.JRException;

public interface IClienteService {

	public List<Cliente> findAll();
	
	public Report reporteCliente()throws Exception,JRException, IOException,  ParseException, DocumentException;
	
	public Cliente findById(Long id);
	
	public void save(Cliente cliente);
	
	public void deleteById(Long id);
	
	public List<Cliente> buscarPorDNI(String DNI);
	
	public String exportReport(String reportFormat) throws FileNotFoundException, JRException, IOException,Exception;
	
	public String base64Clientes(String format) throws FileNotFoundException, JRException;
}
