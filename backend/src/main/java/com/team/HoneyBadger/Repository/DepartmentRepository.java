package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Department;
import com.team.HoneyBadger.Repository.Custom.DepartmentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String>, DepartmentRepositoryCustom {
}
