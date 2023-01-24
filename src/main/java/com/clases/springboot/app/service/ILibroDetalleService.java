package com.clases.springboot.app.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.clases.springboot.app.models.entity.LibroDetalle;
import com.clases.springboot.app.models.entity.Report;
import com.clases.springboot.app.models.entity.Tipo;
import com.itextpdf.text.DocumentException;

import net.sf.jasperreports.engine.JRException;

public interface ILibroDetalleService {
	
	public List<LibroDetalle> findAll();
	
	public LibroDetalle findById(Long id);
	
	void deleteById(Long id);
	
	public void save(LibroDetalle libroDetalle);
	
	public List<LibroDetalle> buscarPorLibroDetalle(String dni);
	
	public List<LibroDetalle> findByIdLibroTipoSacramento(Tipo tipoSacramento);
	
	
	public Report reporteLibroBautizoPorPersona(Long idDetalleLibro)throws Exception,JRException, IOException,  ParseException, DocumentException;

	public Report reporteLibroConfirmacionPorPersona(Long idDetalleLibro)throws Exception,JRException, IOException,  ParseException, DocumentException;
	
	//public Report reporteLibroMatrimonioPorPersona(Long idDetalleLibro)throws Exception,JRException, IOException,  ParseException, DocumentException;
}
