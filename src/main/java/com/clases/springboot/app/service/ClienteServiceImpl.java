package com.clases.springboot.app.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clases.springboot.app.models.dao.IClienteDao;
import com.clases.springboot.app.models.dao.ClienteRepository;
import com.clases.springboot.app.models.entity.Cliente;
import com.clases.springboot.app.models.entity.Report;
import com.inagro.springboot.backend.apirest.util.Util;
import com.inagro.springboot.backend.apirest.util.UtilFormat;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private IClienteService clienteServie;
	
	private String tempPath = System.getProperty("java.io.tmpdir");
	
	@Transactional(readOnly= true)
	@Override
	public List<Cliente> findAll() {
		return (List<Cliente>)clienteDao.findAll();
	}

	@Transactional(readOnly= true)
	@Override
	public Cliente findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void save(Cliente cliente) {
		
		clienteDao.save(cliente);
		
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Cliente> buscarPorDNI(String DNI) {
		return (List<Cliente>) clienteDao.buscarPorDNI(DNI);
	}

	@Transactional
	@Override
	public void deleteById(Long id) {
		clienteDao.deleteById(id);
		
	}
	
	public String exportReport(String reportFormat) throws Exception {	
		List<Cliente> clientes= repository.findAll();
		
		//File file = ResourceUtils.getFile("classpath:libros.jrxml");
		InputStream file = getTemplate();
		JasperReport jasperReport = JasperCompileManager.compileReport(file);
		List<Map<String,Object>> mapDataSource = new ArrayList<>();
		for(Cliente cliente : clientes) {
			Map<String,Object> obj = new HashMap<>();
			obj.put("id", cliente.getId());
			obj.put("dni", cliente.getDni());
			obj.put("ape_paterno", cliente.getApePaterno());
			obj.put("ape_materno", cliente.getApeMaterno());
			obj.put("nombre", cliente.getNombre());
			obj.put("sexo", cliente.getSexo());
			obj.put("fecha_nacimiento", cliente.getFechasNacimiento());
			obj.put("celular1", cliente.getCelular1());
			obj.put("correo", cliente.getCorreo());
			obj.put("direccion", cliente.getDireccion());
			mapDataSource.add(obj);
		}
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mapDataSource);

		java.util.Map<String, Object> parameters = new HashMap<>();
		parameters.put("parametro1", "Lista de Clientes");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
		
		String pathFile = getFormatExport(jasperPrint, reportFormat, "clientes"+"."+reportFormat);
		String archivoCodificado = convertBytes(pathFile);
		deleteFile(pathFile);
		

		return archivoCodificado;
	}

	private void deleteFile(String pathFile) throws Exception {
		// TODO Auto-generated method stub
		File file = new File(pathFile);

		if (!file.delete()) {
			throw new Exception("Ocurrió un problema al eliminar el archivo");
		}
	}

	private String convertBytes(String pathFile) throws IOException {
		byte[] input_file = Files.readAllBytes(Paths.get(pathFile));
		byte[] encodedBytes = Base64.getEncoder().encode(input_file);
		String encodedString = new String(encodedBytes);

		return encodedString;
	}

	@Override
	public String base64Clientes(String format) throws FileNotFoundException, JRException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private InputStream getTemplate() {
		InputStream file;
		file=getClass().getResourceAsStream("/static/Report/listarClientes.jrxml");
		return file;
	}
	
	private String getFormatExport(JasperPrint jasperPrint, String format, String filename) throws JRException {
		String path="";
		path = tempPath + "/" + filename;
		path = replacePaths(path);
		if(format.contentEquals("html")) {
			JasperExportManager.exportReportToHtmlFile(jasperPrint, path);
			
		}
		if(format.contentEquals("pdf")) {
			JasperExportManager.exportReportToPdfFile(jasperPrint, path);
		}
		return path;
	}

	private String replacePaths(String path) {
		return path.replace("\\","\\\\");
	}
	
	
	 //Reporte
    @Override
	 public Report reporteCliente()throws Exception,JRException, IOException,  ParseException, DocumentException{
		System.out.println(" reporteCliente ***");
   	    String nameFile = "/rpt_reporte_cliente";
		FileOutputStream ficheroPDF = new FileOutputStream(tempPath + nameFile + ".pdf");
		PdfPTable table = null;
		PdfPCell cell = null;
		Document document = new Document(PageSize.A4, 0, 0, 10, 10);
		PdfWriter.getInstance(document, ficheroPDF);

		// <editor-fold defaultstate="collapsed" desc="ESTILOS">
		Font fontSubTitle = new Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL);
		Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD);
		fontCabecera.setColor(BaseColor.WHITE);

		Font fontCabeceraHab = new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD);
		fontCabecera.setColor(BaseColor.BLACK);

		Font fontSubCabecera = new Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL);

		BaseColor colorCabecera = new BaseColor(192, 224, 255);
		BaseColor colorPie = new BaseColor(255, 245, 241);
		// </editor-fold>

		document.open();

		// <editor-fold defaultstate="collapsed" desc="CABECERA">
		PdfPTable cabeceraTable = new PdfPTable(2);
		cell = new PdfPCell();

		Image img = Image.getInstance(this.getClass().getClassLoader().getResource("logo.png"));
		img.scaleToFit(100, 100);

		cell = new PdfPCell(img);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setRowspan(4);
		cabeceraTable.addCell(cell);
			

		cell = new PdfPCell(new Phrase("Fecha : " + UtilFormat.formatearFechaNombreDiaMes(new Date()), fontSubTitle));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cabeceraTable.addCell(cell);
		

		cell = new PdfPCell(new Phrase("Parroquia San Pedro Nolasco"));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cabeceraTable.addCell(cell);
		
		
		//llama toda la cabecera del pdf
		document.add(cabeceraTable);

		saltoLinea(document);

		table = new PdfPTable(4);
		cell = new PdfPCell();

		cell = new PdfPCell(new Phrase("DNI", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Apellidos", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
	
               
		cell = new PdfPCell(new Phrase("Nacimiento", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Correo", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
               
       
	

		table.setWidths(new int[] {10,50,15,25});
		document.add(table);
		
		List<Cliente> findAll = repository.findAll();
		for (Cliente cliente : findAll) {					

			table = new PdfPTable(4);
			cell = new PdfPCell();

			cell = new PdfPCell(new Phrase(cliente.getDni(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
		
			cell = new PdfPCell(new Phrase(cliente.getApePaterno() + " " + cliente.getApeMaterno() + " " + cliente.getNombre(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
                       
           cell = new PdfPCell(new Phrase(cliente.getFechasNacimiento(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);

           cell = new PdfPCell(new Phrase(cliente.getCorreo(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);

			table.setWidths(new int[] {10,50,15,25});
			document.add(table);		
		
		}
               
		document.newPage();
		document.close();
		String pathFile = tempPath + nameFile + ".pdf";
		String archivoBase = Util.convertBytes(pathFile);
		Util.deleteFile(pathFile);
		Report report = new Report();
		report.setPdfBase(archivoBase);
		return report;
	}
	private void saltoLinea(Document document) throws DocumentException {
		document.add(new Paragraph(Chunk.NEWLINE));
	}
	private PdfPTable agregarLogo() throws BadElementException, MalformedURLException, IOException {
		PdfPCell cell;
		PdfPTable cabeceraTable = new PdfPTable(2);
		cell = new PdfPCell();

		Image img = Image.getInstance(this.getClass().getClassLoader().getResource("logo.png"));
		img.scaleToFit(100, 100);

		cell = new PdfPCell(img);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setRowspan(4);
		cabeceraTable.addCell(cell);
		return cabeceraTable;
	}

	@Override
	public List<Cliente> findByDni(String dni) {
		return (List<Cliente>)clienteDao.findByDni(dni);

	}
	
	
	
	
	
	

}
