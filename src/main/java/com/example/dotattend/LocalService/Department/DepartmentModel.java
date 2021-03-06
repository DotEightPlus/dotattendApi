package com.example.dotattend.LocalService.Department;

import javax.persistence.*;

@Entity
@Table(name = "departments")
public class DepartmentModel {
    @Id
    private int id;
    private String department;

    public DepartmentModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
