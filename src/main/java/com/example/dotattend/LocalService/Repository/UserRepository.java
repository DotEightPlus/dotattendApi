package com.example.dotattend.LocalService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    @Query(value = "select * from userinformation where matricno=?1",nativeQuery = true)
    Optional<User> findByMatricNo(String matricno);
}
