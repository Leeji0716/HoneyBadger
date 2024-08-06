package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Department;
import com.team.HoneyBadger.Enum.DepartmentRole;
import com.team.HoneyBadger.Repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Department save(String name, Department parent, DepartmentRole role) {
        return departmentRepository.save(Department.builder().name(name).parent(parent).role(role).build());
    }

    @Transactional
    public Department get(String id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public List<Department> getTopList() {
        return departmentRepository.getTopList();
    }

    @Transactional
    public void delete(Department department) {
        departmentRepository.delete(department);
    }


}
