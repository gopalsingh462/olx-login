package com.zensar.olx.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zensar.olx.entity.UserEntity;
import com.zensar.olx.repo.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	UserRepo userRepo;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserEntity> userEntityList = userRepo.findByUsername(username);
		if(userEntityList.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		UserEntity userEntity = userEntityList.get(0);
		List<GrantedAuthority> list = new ArrayList<>();
		list.add(new SimpleGrantedAuthority(userEntity.getRoles()));
		User user = new User(userEntity.getUsername(), passwordEncoder.encode(userEntity.getPassword()), list);
		return user;
	}
	
	

}
