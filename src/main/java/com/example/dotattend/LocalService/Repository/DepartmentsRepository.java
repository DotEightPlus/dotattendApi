package com.example.dotattend.LocalService.Repository;

import com.example.dotattend.LocalService.Department.DepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentsRepository extends JpaRepository<DepartmentModel,Integer> {
}
