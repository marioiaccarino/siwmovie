package it.uniroma3.siw.controller.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;

@Component
public class ReviewValidator implements org.springframework.validation.Validator{
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review)o;

		List<Review> l =(List<Review>)this.reviewRepository.findAll();

		for(Review r : l)	{
				if(r.getReviewer().equals(review.getReviewer()))	{
					if(r.getMovieReviewed().equals(review.getMovieReviewed()))	{
						errors.reject("review.writtenYet");

					}
				}
		}
	}
	
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
	
	public User getCurrentUser()	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User u = credentials.getUser();
		return u;
	}
}
