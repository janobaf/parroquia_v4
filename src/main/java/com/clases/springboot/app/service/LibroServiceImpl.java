package com.clases.springboot.app.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clases.springboot.app.models.dao.ILibroDao;
import com.clases.springboot.app.models.dao.LibroRepository;
import com.clases.springboot.app.models.entity.Acta;
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
@Service
public class LibroServiceImpl implements ILibroService {
	
	@Autowired
	private LibroRepository repository;
	
	@Autowired
	private ILibroService libroServie;
	
	private String tempPath = System.getProperty("java.io.tmpdir");

	@Autowired
	private ILibroDao libroDao;
	@Transactional (readOnly= true)
	@Override
	public List<Libro> findAll() {
		return (List<Libro>)libroDao.findAll();
	}

	@Transactional (readOnly= true)
	@Override
	public Libro findById(Long id) {
		return libroDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public void deleteById(Long id) {
		libroDao.deleteById(id);		
	}

	@Transactional
	@Override
	public void save(Libro libro) {
		libroDao.save(libro);		
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Libro> buscarPorLibro(String numTomo) {
		return (List<Libro>) libroDao.buscarPorLibro(numTomo);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Libro> buscarLibroNombre(String tipoSacramento) {
		return (List<Libro>) libroDao.buscarPorLibro(tipoSacramento);
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

     //Reporte
     @Override
	 public Report reporteLibro()throws Exception,JRException, IOException,  ParseException, DocumentException{
		System.out.println(" reporteLibro ***");
    	 String nameFile = "/rpt_reporte_libro";
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

		table = new PdfPTable(5);
		cell = new PdfPCell();

		cell = new PdfPCell(new Phrase("Sacramento", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Num Tomo", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
	
                
		cell = new PdfPCell(new Phrase("Fecha Apertura", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Fecha Cierre", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Num Pag", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
		cell.setBackgroundColor(colorCabecera);
		table.addCell(cell);
		
	      
	

		table.setWidths(new int[] {20,20,20,20,20});
		document.add(table);
		
		List<Libro> findAll = repository.findAll();
		for (Libro libro : findAll) {					

			table = new PdfPTable(5);
			cell = new PdfPCell();

			cell = new PdfPCell(new Phrase(libro.getTipoSacramento().getNombre(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
		
			cell = new PdfPCell(new Phrase(String.valueOf(libro.getNumTomo()), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
                        
            cell = new PdfPCell(new Phrase(libro.getFechaApertura().toString(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);

            cell = new PdfPCell(new Phrase(libro.getFechaCierre().toString(), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(String.valueOf(libro.getNumPaginas()), fontCabecera));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
			table.addCell(cell);
			
					
			table.setWidths(new int[] {20,20,20,20,20});
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
		cell.setRowspan(5);
		cabeceraTable.addCell(cell);
		return cabeceraTable;
	}
	
}
