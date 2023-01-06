package com.example.ss_les2.repositories;

import com.example.ss_les2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//@Repository // не потрібно 1) це interface 2) спрінг буже знати що це бін, бо  extends JpaRepository

public interface UserRepository extends JpaRepository<User, Long> {
    // нагадування, що тут це JPQL, тобто працюємо з класом Ентіті, а не з самою таблицею
    @Query("SELECT u from User u where u.username = :username")
    Optional<User> findByUsername(String username);
}