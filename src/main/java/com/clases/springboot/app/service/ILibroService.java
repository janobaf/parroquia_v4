package com.clases.springboot.app.service;


import java.io.IOException;
import java.util.List;

import com.clases.springboot.app.models.entity.Libro;
import com.clases.springboot.app.models.entity.Report;
import com.itextpdf.text.DocumentException;
import java.text.ParseException;

import net.sf.jasperreports.engine.JRException;

public interface ILibroService {
	
	public List<Libro> findAll();
	
	public Report reporteLibro()throws Exception,JRException, IOException,  ParseException, DocumentException;
	
	
	Libro findById(Long id);
	
	void deleteById(Long id);
	
	public void save(Libro libro);
	
	public List<Libro> buscarPorLibro(String numTomo);
	
	public List<Libro> buscarLibroNombre(String tipoSacramento);
        
	
}
