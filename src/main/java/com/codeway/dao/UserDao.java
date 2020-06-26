package com.codeway.dao;

import com.codeway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	List<User> findByAccountType(String accountType);
}
