package com.zensar.olx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zensar.olx.dto.AuthenticationRequest;
import com.zensar.olx.dto.LoginResponse;
import com.zensar.olx.dto.User;
import com.zensar.olx.entity.UserEntity;
import com.zensar.olx.repo.UserRepo;
import com.zensar.olx.security.JwtUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("olx/authentication")
public class UserController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	UserRepo userRepo;

	@Autowired
	UserDetailsService userDetailsService;

	@PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticates the user and return the auth token")
	public ResponseEntity<LoginResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
		LoginResponse res = new LoginResponse();
		try {
			this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));

		} catch (BadCredentialsException exception) {
			res.setLoginSuccess(false);
			res.setToken(null);
			return new ResponseEntity<LoginResponse>(res, HttpStatus.BAD_REQUEST);
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		String jwtToken = jwtUtil.generateToken(userDetails);
		res.setLoginSuccess(true);
		res.setToken(jwtToken);
		return new ResponseEntity<LoginResponse>(res, HttpStatus.OK);
	}

	@GetMapping(value = "/validatetoken")
	@ApiOperation(value = "Validates the token and return true if validation successfully and false otherwise")
	public ResponseEntity<User> validateToken(@RequestHeader(value = "Authorization") String authToken) {
		try {
			String token = authToken.substring(7);
			String username = jwtUtil.extractUsername(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			boolean isTokenValid = jwtUtil.validateToken(token, userDetails);
			if (isTokenValid) {
				UserEntity userEntity = userRepo.findByUsername(username).get(0);
				User user = new User();
				user.setId(userEntity.getId());
				user.setUsername(userEntity.getUsername());
				user.setRoles(userEntity.getRoles());
				return new ResponseEntity<User>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<User>(new User(), HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<User>(new User(), HttpStatus.OK);
		}
	}

}
