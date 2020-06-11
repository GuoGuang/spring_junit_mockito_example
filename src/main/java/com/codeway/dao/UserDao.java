package com.codeway.dao;

import com.codeway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	List<User> findByAccountType(String accountType);
}
