package com.clases.springboot.app.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clases.springboot.app.models.dao.ClienteRepository;
import com.clases.springboot.app.models.dao.EmpleadoRepository;
import com.clases.springboot.app.models.dao.IEmpleadoDao;
import com.clases.springboot.app.models.entity.Empleado;
import com.clases.springboot.app.models.entity.Libro;
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

@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

	@Autowired
	private IEmpleadoDao empleadoDao;
	
	@Autowired
	private EmpleadoRepository repository;
	
	@Autowired
	private IEmpleadoService empleadoServie;
	
	private String tempPath = System.getProperty("java.io.tmpdir");
	
	@Transactional(readOnly=true)
	@Override
	public List<Empleado> findAll() {
		return (List<Empleado>)empleadoDao.findAll();
	}

	@Transactional(readOnly=true)
	@Override
	public Empleado findById(Long id) {
		return empleadoDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void save(Empleado empleado) {
		empleadoDao.save(empleado);		
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Empleado> buscarPorDNI(String DNI) {
		return (List<Empleado>) empleadoDao.buscarPorDNI(DNI);
	}

	
	@Transactional
	@Override
	public void deleteById(Long id) {
		empleadoDao.deleteById(id);
	}
	
	
	//Reporte
    @Override
	 public Report reporteEmpleado()throws Exception,JRException, IOException,  ParseException, DocumentException{
    	
		System.out.println(" reporteEmpleado ***");
   	    String nameFile = "/rpt_reporte_empleado";
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
		cell.setRowspan(6);
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

		table = new PdfPTable(6);
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
	
               
		cell = new PdfPCell(new Phrase("Cargo", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Fecha Inicio", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Fecha Baja", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Estado", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);

		table.setWidths(new int[] {10,35,15,15,15,10});
		document.add(table);
		
		List<Empleado> findAll = repository.findAll();
		for (Empleado empleado : findAll) {					

			table = new PdfPTable(6);
			cell = new PdfPCell();
			
			cell = new PdfPCell(new Phrase(empleado.getDni(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);


			cell = new PdfPCell(new Phrase(empleado.getApePaterno() + " " + empleado.getApeMaterno() + " " + empleado.getNombres() , fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);			
			
			
			cell = new PdfPCell(new Phrase(String.valueOf(empleado.getCargoId().getNombre()), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
                       
          
            cell = new PdfPCell(new Phrase(empleado.getFechaInicio().toString(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
			
			
			cell = new PdfPCell(new Phrase(empleado.getFechaBaja().toString(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
	
			
			Phrase phrase = new Phrase(String.valueOf(empleado.getEstado()), fontCabecera);
			cell = new PdfPCell(phrase);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);

			table.setWidths(new int[] {10,35,15,15,15,10});
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
		cell.setRowspan(6);
		cabeceraTable.addCell(cell);
		return cabeceraTable;
	}
	

}
