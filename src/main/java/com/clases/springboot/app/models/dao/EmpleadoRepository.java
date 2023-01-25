package com.clases.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clases.springboot.app.models.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

}
