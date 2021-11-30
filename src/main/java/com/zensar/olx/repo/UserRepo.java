package com.zensar.olx.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zensar.olx.entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Integer>{
	List<UserEntity> findByUsername(String username);
}
