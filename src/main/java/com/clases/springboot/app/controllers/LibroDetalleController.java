package com.clases.springboot.app.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.clases.springboot.app.models.entity.Cliente;
import com.clases.springboot.app.models.entity.Empleado;
import com.clases.springboot.app.models.entity.LibroDetalle;
import com.clases.springboot.app.models.entity.Report;
import com.clases.springboot.app.models.entity.Tipo;
import com.clases.springboot.app.service.IClienteService;
import com.clases.springboot.app.service.IEmpleadoService;
import com.clases.springboot.app.service.ILibroDetalleService;
import com.clases.springboot.app.service.IReportService;
import com.itextpdf.text.DocumentException;

import net.sf.jasperreports.engine.JRException;

@Controller
@SessionAttributes("libroDetalle")
public class LibroDetalleController {

	@Autowired
	private ILibroDetalleService libroDetalleService;	
	
	@Autowired
	private IEmpleadoService empleadoService;
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IReportService reportService;
	
	
	
	@RequestMapping(value= {"/listarLibroBautizo"}, method=RequestMethod.GET)
	public String listarBautizo(Model model) {
		model.addAttribute("titulo", "Registro Libros Bautizo");
		Tipo tipoSacramento=new Tipo();
		tipoSacramento.setId(Long.valueOf(1));
		List<LibroDetalle> findByIdLibroTipoSacramento = libroDetalleService.findByIdLibroTipoSacramento(tipoSacramento);
		model.addAttribute("librosBautizo", findByIdLibroTipoSacramento);
		return "libroDetalle/listarLibroBautizo";
	}
	
	
	@RequestMapping(value= {"/listarLibroConfirmacion"}, method=RequestMethod.GET)
	public String listarConfirmacion(Model model) {
		model.addAttribute("titulo", "Registro Libros Confirmaci??n");
		Tipo tipoSacramento=new Tipo();
		tipoSacramento.setId(Long.valueOf(2));
		List<LibroDetalle> findByIdLibroTipoSacramento = libroDetalleService.findByIdLibroTipoSacramento(tipoSacramento);
		model.addAttribute("listarConfirmacion", findByIdLibroTipoSacramento);
		return "libroDetalle/listarLibroConfirmacion";
	}
	
	@RequestMapping(value= {"/listarLibroMatrimonio"}, method=RequestMethod.GET)
	public String listarMatrimonio(Model model) {
		model.addAttribute("titulo", "Registro Libros Matrimoio");
		Tipo tipoSacramento=new Tipo();
		tipoSacramento.setId(Long.valueOf(3));
		List<LibroDetalle> findByIdLibroTipoSacramento = libroDetalleService.findByIdLibroTipoSacramento(tipoSacramento);
		model.addAttribute("listarMatrimonio", findByIdLibroTipoSacramento);
		return "libroDetalle/listarLibroMatrimonio";
	}
	
	
	
	@RequestMapping(value= {"/formLibroBautizo"} )
	public String crearBautizo(Map<String, Object> model) {
		model.put("titulo", "Formulario de Libro Bautizo");		
		LibroDetalle libroBautizo = new LibroDetalle();
		model.put("librosBautizo", libroBautizo);
		List<Empleado> findByCargoIdId = empleadoService.findByCargoIdId(Long.valueOf(2));
		model.put("parrocos", findByCargoIdId);
		List<Cliente> clienteFindAll = clienteService.findAll();
		model.put("clientes", clienteFindAll);
		return "libroDetalle/formLibroBautizo";
	}
	
	
	@RequestMapping(value="/formLibroBautizo/{id}" )
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model,RedirectAttributes f) {
		LibroDetalle libroBautizo = null;
		model.put("librosBautizo", libroBautizo);
		List<Empleado> findByCargoIdId = empleadoService.findByCargoIdId(Long.valueOf(2));
		model.put("parrocos", findByCargoIdId);
		List<Cliente> clienteFindAll = clienteService.findAll();
		model.put("clientes", clienteFindAll);


		if (id>0) {
			libroBautizo = libroDetalleService.findById(id);
			if(libroBautizo == null) {
				f.addFlashAttribute("error","El ID del Libro no existe en la BD");
				return "redirect:/libroDetalle/formLibroBautizo";
			}
		}
		else {
			f.addFlashAttribute("error","El ID del Libro no puede ser cero!");
			return "redirect:/libroDetalle/formLibroBautizo";			
		}
		
		
		model.put("librosBautizo", libroBautizo);
		model.put("titulo", "Editar Libro");
		return "libroDetalle/formLibroBautizo";
	}
	
	
	
	
	@RequestMapping(value= {"/formLibroConfirmacion"} )
	public String crearConfirmacion(Map<String, Object> model) {
		model.put("titulo", "Formulario de Libro Confirmacion");		
		LibroDetalle libroConfirmacion = new LibroDetalle();
		model.put("librosConfirmacion", libroConfirmacion);
		return "libroDetalle/formLibroConfirmacion";
	}
	
	@RequestMapping(value= {"/formLibroMatrimonio"} )
	public String crearMatrimonio(Map<String, Object> model) {
		model.put("titulo", "Formulario de Libro Matrimonio");		
		LibroDetalle libroMatrimonio = new LibroDetalle();
		model.put("librosMatrimonio", libroMatrimonio);
		return "libroDetalle/formLibroMatrimonio";
	}
	
	
	@RequestMapping(value= {"/formLibroBautizo"}, method=RequestMethod.POST)
	public String guardarLibroBautizo(@Valid LibroDetalle libroDetalle, BindingResult result, Model model,RedirectAttributes f, SessionStatus status) {
		
		
		
		if(result.hasErrors() || !libroDetalleService.registrable(libroDetalle,1L)) {
			LibroDetalle libroBautizo = libroDetalle;
			List<Empleado> findByCargoIdId = empleadoService.findByCargoIdId(Long.valueOf(2));
			model.addAttribute("parrocos", findByCargoIdId);
			List<Cliente> clienteFindAll = clienteService.findAll();
			model.addAttribute("clientes", clienteFindAll);
			model.addAttribute("librosBautizo", libroBautizo);
			model.addAttribute("titulo", "Formulario de Libro Bautizo");
			model.addAttribute("error", "La persona ya ha sido bautizada anteriormente");
			return "libroDetalle/formLibroBautizo";
		}else {
			libroDetalleService.save(libroDetalle);

		}
	
		
		status.setComplete();
		f.addFlashAttribute("success","Grabado con exito");
		return "redirect:listarLibroBautizo";
	}



	
	//listar por tomo
		@RequestMapping(value="listarLibroBautizo/{sacramento}", method=RequestMethod.GET)
		public String filtrar(@PathVariable(value="sacramento") int tipo, Model model ) {
				model.addAttribute("titulo", "Listado de Libros");
				//libroDetalleService.buscarPorLibroDetalle(dni);				
				Tipo tipoSacramento=new Tipo();
				tipoSacramento.setId(Long.valueOf(tipo));
				List<LibroDetalle> findByIdLibroTipoSacramento = libroDetalleService.findByIdLibroTipoSacramento(tipoSacramento);
				model.addAttribute("libros", findByIdLibroTipoSacramento);
				return "libro/listarLibro";
		}
		
		
		@GetMapping("/replibroBautizoPersona/{id}")
		@ResponseBody
		public Report reporteLibroBautizoPorPersona(@PathVariable Long id) throws JRException, IOException, ParseException, DocumentException, Exception{
		
			return libroDetalleService.reporteLibroBautizoPorPersona(id);
		}
		
		
		@GetMapping("/replibroConfirmacionPersona/{id}")
		@ResponseBody
		public Report reportelibroConfirmacionPersona(@PathVariable Long id) throws JRException, IOException, ParseException, DocumentException, Exception{
			
			return libroDetalleService.reporteLibroConfirmacionPorPersona(id);
		}
		
		@RequestMapping(value= {"/reportAnioMes"}, method=RequestMethod.GET)
		public String findByYearAndMonth(@RequestParam(value="anio",required=false) Integer anio,
				   @RequestParam(value="mes",required=false) Integer mes,
					Model model){
			model.addAttribute("titulo", "Listado de Libros");
			model.addAttribute("libros", libroDetalleService.buscarPorAnioAndMes(anio, mes));

			return "libro/listarLibro";
			
		}
		
		@GetMapping("/rptLibroDetalleByAnioAndMes")
		@ResponseBody
		public Report reporteLibroDetalle(@PathVariable Long id) throws JRException, IOException, ParseException, DocumentException, Exception{
		
			return reportService.reporteLibroDetalleByAnioAndMes(2022, 1);
		}
		
		
		
	
}
