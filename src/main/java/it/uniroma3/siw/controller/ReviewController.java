package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.controller.validator.ReviewValidator;

import it.uniroma3.siw.model.Review;

import it.uniroma3.siw.service.ReviewService;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	
	@Autowired
	private ReviewValidator reviewValidator;


	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/user/index/{idu}")	
	public String indexUSer(@PathVariable("id") Long id,Model model)	{
		this.reviewService.indexUser(id, model);
		return "user/index.html";
	}
	
	@GetMapping("/user/movie/{idu}")
	public String userMovie(@PathVariable("idu") Long idu, Model model)	{
		this.reviewService.userMovie(idu, model);
		model.addAttribute("user", this.reviewService.getCurrentUser());
		return "user/movie.html";
	}
	
	@GetMapping("/user/leaveReview/{idm}")
	public String leaveReview(@PathVariable ("idm") Long idm, Model model)	{
		this.reviewService.leaveReview(idm, model);
		return "user/formNewReview.html";
	}
	
	@PostMapping("user/review/{idm}")
	public String review(@PathVariable Long idm, @Valid @ModelAttribute("review") Review review,BindingResult bindingResult, Model model)	{
		
		this.reviewService.reviewEntry(idm, review);
		
		this.reviewValidator.validate(review, bindingResult);
		if(!bindingResult.hasErrors())	{
			this.reviewService.reviewSuccess(idm, review, model);
			return "user/review.html";
		}
		this.reviewService.reviewFail(idm, review, model);
		return "user/formNewReview.html";
	}
	

	
	@GetMapping("/review/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		this.reviewService.getReview(id, model);
		return "review.html";
	}
	
	@GetMapping("/reviews/{id}")
	public String getMovies(@PathVariable("id") Long id, Model model) {	
		this.reviewService.getReviews(id, model);
		return "reviews.html";
	}

}
