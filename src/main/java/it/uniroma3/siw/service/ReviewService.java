package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.model.*;

@Service
public class ReviewService {
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public void leaveReview(Long idm, Model model)	{
		Movie m = this.movieRepository.findById(idm).get();
		Review r = new Review();
		m.getMovieReviews().add(r);
		r.setMovieReviewed(m);
		model.addAttribute("movie", m);
		model.addAttribute("review", r);
		return ;
	}
	
	@Transactional
	public void reviewEntry(Long idm, Review r)	{
		r.setReviewer(this.getCurrentUser());
		Movie m = this.movieRepository.findById(idm).get();
		r.setMovieReviewed(m);
		m.getMovieReviews().add(r);
		return;
	}
	@Transactional
	public void reviewSuccess(Long idm, Review r, Model model)	{
		Movie m = this.movieRepository.findById(idm).get();
		r.setReviewer(getCurrentUser());
		r.setMovieReviewed(m);
		m.getMovieReviews().add(r);
		this.reviewRepository.save(r);
		model.addAttribute("user", this.getCurrentUser());
		return;
		
	}
	
	//v
	@Transactional
	public void indexUser(Long id, Model model)	{
		model.addAttribute("user", this.userRepository.findById(id).get());
		return;
	}
	
	public void userMovie(Long idu, Model model)	{
		model.addAttribute("movies", this.movieRepository.findAll());
		model.addAttribute("user", this.getCurrentUser());
		return;
	}
	@Transactional
	public void reviewFail(Long idm, Review r, Model model)	{
		this.reviewRepository.delete(r);
		model.addAttribute("movie", this.movieRepository.findById(idm).get());
		model.addAttribute("review", r);
		return ;
	}
	
	@Transactional
	public void getReviews(Long idm, Model model) {
		Movie m = this.movieRepository.findById(idm).get();
		model.addAttribute("movie", m);
		List<Review> filmReviews = new ArrayList<>();
		for(Review r : this.reviewRepository.findAll())	{
			if(r.getMovieReviewed().equals(m))	{
				filmReviews.add(r);
			}
		}
		model.addAttribute("reviews", filmReviews);
		model.addAttribute("user", this.getCurrentUser());
		return;
	}
	
	@Transactional
	public void getReview(Long id, Model model) {
		model.addAttribute("review", this.reviewRepository.findById(id).get());
		model.addAttribute("user", this.getCurrentUser());
		return;
	}
	
	public User getCurrentUser()	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User u = credentials.getUser();
		return u;
	}
}
