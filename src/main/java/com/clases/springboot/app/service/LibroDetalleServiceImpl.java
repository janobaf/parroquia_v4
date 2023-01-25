package com.clases.springboot.app.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clases.springboot.app.models.dao.IClienteDao;
import com.clases.springboot.app.models.dao.ILibroDetalleDao;
import com.clases.springboot.app.models.dao.LibroDetalleRepository;
import com.clases.springboot.app.models.entity.Cliente;
import com.clases.springboot.app.models.entity.Empleado;
import com.clases.springboot.app.models.entity.LibroDetalle;
import com.clases.springboot.app.models.entity.Report;
import com.clases.springboot.app.models.entity.Tipo;
import com.inagro.springboot.backend.apirest.util.Util;
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
public class LibroDetalleServiceImpl implements ILibroDetalleService {
	
	@Autowired
	private LibroDetalleRepository repository;
	
	@Autowired
	private ILibroDetalleDao libroDetalleDao;
	
	@Autowired
	private IClienteDao clienteDao;
	
	
	private String tempPath = System.getProperty("java.io.tmpdir");

	
	@Transactional (readOnly= true)
	@Override
	public List<LibroDetalle> findAll() {
		return (List<LibroDetalle>)libroDetalleDao.findAll();
	}

	@Transactional (readOnly= true)
	@Override
	public LibroDetalle findById(Long id) {
		return libroDetalleDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public void deleteById(Long id) {
		libroDetalleDao.deleteById(id);		
	}

	@Transactional
	@Override
	public void save(LibroDetalle libroDetalle) {
		List<Cliente> findByDni = clienteDao.findByDni(libroDetalle.getIdCliente().getDni());
		if(findByDni.size()==0) {
			clienteDao.save(libroDetalle.getIdCliente());
		}else {
			Cliente clienteNew = libroDetalle.getIdCliente();
			
			clienteNew.setId(findByDni.get(0).getId());
		}
		libroDetalleDao.save(libroDetalle);		
	}

	
	
	
	
	
	@Transactional(readOnly=true)
	@Override
	public List<LibroDetalle> buscarPorLibroDetalle(String dni) {
		return (List<LibroDetalle>) libroDetalleDao.buscarPorLibroDetalleDni(dni);
	}
	
	
	
	@Transactional(readOnly=true)
	@Override
	public List<LibroDetalle> findByIdLibroTipoSacramento(Tipo tipoSacramento){
		return (List<LibroDetalle>) libroDetalleDao.findByIdLibroTipoSacramento(tipoSacramento);
	}
		
		
	
	//Reporte Libro Bautizo
    @Override
	 public Report reporteLibroBautizoPorPersona(Long idDetalleLibro)throws Exception,JRException, IOException,  ParseException, DocumentException{
    	LibroDetalle detalle = findById(idDetalleLibro);
		System.out.println(" reporteLibroBautizoPersona ***");
   	    String nameFile = "/rpt_reporte_libro_bautizo_persona";
		FileOutputStream ficheroPDF = new FileOutputStream(tempPath + nameFile + ".pdf");
		PdfPTable table = null;
		PdfPCell cell = null;
		Document document = new Document(PageSize.A4, 0, 0, 10, 10);
		PdfWriter.getInstance(document, ficheroPDF);
		
		Font fontSubTitle = new Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL);
		Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL);
		fontCabecera.setColor(BaseColor.WHITE);

		Font fontCabeceraHab = new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD);
		fontCabecera.setColor(BaseColor.BLACK);

		Font fontSubCabecera = new Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL);

		BaseColor colorCabecera = new BaseColor(192, 224, 255);
		BaseColor colorPie = new BaseColor(255, 245, 241);

		document.open();

		// <editor-fold defaultstate="collapsed" desc="CABECERA">
		PdfPTable cabeceraTable = new PdfPTable(1);
		cell = new PdfPCell();

		Image img = Image.getInstance(this.getClass().getClassLoader().getResource("logo.png"));
		img.scaleToFit(100, 100);

		cell = new PdfPCell(img);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setRowspan(2);
		cabeceraTable.addCell(cell);

		//llama toda la cabecera del pdf
		document.add(cabeceraTable);

		saltoLinea(document);

		table = new PdfPTable(2);
		cell = new PdfPCell();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MM-yyyy");

        LocalDate localDate = LocalDate.parse(detalle.getFechaSacramento(),df);
		cell = new PdfPCell(new Phrase(
				  "N°            : "+detalle.getNumRegistro()+ "\n"
				+ "Num pag : "+detalle.getNumPag()+ "\n"
				+ "Fecha      : "+localDate+" \n"
				//+ "Registro Civil\n"
				//+ "Año "+localDate.getYear()+" Tomo "+detalle.getIdLibro().getNumTomo()+ "\n"
				//+ "Folio "+detalle.getIdLibro().getNumTomo()
				//+ "Acta ......\n"				
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(cell);
			
		cell = new PdfPCell(new Phrase(
				  "Año del Señor del " + localDate.getYear() 
				+ " en esta Iglesia Parroquial de San Pedro Nolasco, yo " + detalle.getParroco().getNombres() + " " + detalle.getParroco().getApeMaterno() + " " + detalle.getParroco().getApeMaterno() 
				+ " bautizo Solemnemente a un(a) niño(a) en Trujillo el día " + localDate.getDayOfMonth()
				+ " del mes de " + localDate.getMonth()
				+ " del año " + localDate.getYear() 
				+ " hijo(a) de Don "
				+ " y de Doña " 
				+ " natural de Trujillo , residente en Trujillo, "
				+ " , Púsele por nombre " + detalle.getIdCliente().getNombre()
				+ " Fueron Padrinos Don " + detalle.getPadrino()
				+ " y Doña " + detalle.getMadrina() + " de lo que doy fé. \n \n \n"
				+ "                    Lo certifico \n"
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		table.setWidths(new int[] {30,70});
		document.add(table);
					
		PdfPTable firmaTable1 = new PdfPTable(2);
		cell = new PdfPCell(new Phrase("", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable1.addCell(cell);
	
               
		Image firma = Image.getInstance(this.getClass().getClassLoader().getResource("firmas/"+detalle.getParroco().getDni().trim()+".png"));
		firma.scaleToFit(100, 100);

		cell = new PdfPCell(firma);

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable1.addCell(cell);       
	
		table.setWidths(new int[] {30,70});
		document.add(firmaTable1);
		
		PdfPTable firmaTable2 = new PdfPTable(2);
		cell = new PdfPCell(new Phrase("", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable2.addCell(cell);

		cell = new PdfPCell(new Phrase("PARROCO"
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable2.addCell(cell);
       	

		table.setWidths(new int[] {30,70});
		document.add(firmaTable2);
		               
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
	
	
	//Reporte Libro Confirmacion
    @Override
	 public Report reporteLibroConfirmacionPorPersona(Long idDetalleLibro)throws Exception,JRException, IOException,  ParseException, DocumentException{
    	LibroDetalle detalle = findById(idDetalleLibro);
		System.out.println(" reporteLibroConfirmacionPersona ***");
   	    String nameFile = "/rpt_reporte_libro_confirmacion_persona";
		FileOutputStream ficheroPDF = new FileOutputStream(tempPath + nameFile + ".pdf");
		PdfPTable table = null;
		PdfPCell cell = null;
		Document document = new Document(PageSize.A4, 0, 0, 10, 10);
		PdfWriter.getInstance(document, ficheroPDF);
		
		Font fontSubTitle = new Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL);
		Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL);
		fontCabecera.setColor(BaseColor.WHITE);

		Font fontCabeceraHab = new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD);
		fontCabecera.setColor(BaseColor.BLACK);

		Font fontSubCabecera = new Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL);

		BaseColor colorCabecera = new BaseColor(192, 224, 255);
		BaseColor colorPie = new BaseColor(255, 245, 241);

		document.open();

		// <editor-fold defaultstate="collapsed" desc="CABECERA">
		PdfPTable cabeceraTable = new PdfPTable(1);
		cell = new PdfPCell();

		Image img = Image.getInstance(this.getClass().getClassLoader().getResource("logo.png"));
		img.scaleToFit(100, 100);

		cell = new PdfPCell(img);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setRowspan(2);
		cabeceraTable.addCell(cell);

		//llama toda la cabecera del pdf
		document.add(cabeceraTable);

		saltoLinea(document);

		table = new PdfPTable(2);
		cell = new PdfPCell();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MM-yyyy");

        LocalDate localDate = LocalDate.parse(detalle.getFechaSacramento(),df);
		cell = new PdfPCell(new Phrase(
				  "N°            : "+detalle.getNumRegistro()+ "\n"
				+ "Num pag : "+detalle.getNumPag()+ "\n"
				+ "Fecha      : "+localDate+" \n"			
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(cell);
			
		cell = new PdfPCell(new Phrase(
				  "Año del Señor del " + localDate.getYear() 
				+ " en esta Iglesia Parroquial de San Pedro Nolasco, yo " + detalle.getParroco().getNombres() + " " + detalle.getParroco().getApeMaterno() + " " + detalle.getParroco().getApeMaterno() 
				+ " confirmo Solemnemente en Trujillo el día " + localDate.getDayOfMonth()
				+ " del mes de " + localDate.getMonth()
				+ " del año " + localDate.getYear() 				
				+ " natural de Trujillo , residente en Trujillo, "
				+ " , Púsele por nombre " + detalle.getIdCliente().getNombre() + " " + detalle.getIdCliente().getApePaterno() + " " + detalle.getIdCliente().getApeMaterno()
				+ " Fueron Padrinos Don " + detalle.getPadrino()
				+ " y Doña " + detalle.getMadrina() + " de lo que doy fé. \n \n \n"
				+ "                    Lo certifico \n"
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		table.setWidths(new int[] {30,70});
		document.add(table);
					
		PdfPTable firmaTable1 = new PdfPTable(2);
		cell = new PdfPCell(new Phrase("", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable1.addCell(cell);
	
               
		Image firma = Image.getInstance(this.getClass().getClassLoader().getResource("firmas/"+detalle.getParroco().getDni().trim()+".png"));
		firma.scaleToFit(100, 100);

		cell = new PdfPCell(firma);

		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable1.addCell(cell);       
	
		table.setWidths(new int[] {30,70});
		document.add(firmaTable1);
		
		PdfPTable firmaTable2 = new PdfPTable(2);
		cell = new PdfPCell(new Phrase("", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable2.addCell(cell);

		cell = new PdfPCell(new Phrase("PARROCO"
				+ "", fontCabecera));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(Rectangle.NO_BORDER);
		firmaTable2.addCell(cell);
       	

		table.setWidths(new int[] {30,70});
		document.add(firmaTable2);
		               
		document.newPage();
		document.close();
		String pathFile = tempPath + nameFile + ".pdf";
		String archivoBase = Util.convertBytes(pathFile);
		Util.deleteFile(pathFile);
		Report report = new Report();
		report.setPdfBase(archivoBase);
		return report;
	}    
	
	
}
