package it.uniroma3.siw.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;
import java.util.*;

@Controller
public class MovieController {
	
	@Autowired 
	private MovieRepository movieRepository;
	
	@Autowired 
	private MovieValidator movieValidator;

	@Autowired
	private MovieService movieService;
	
	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value="/admin/movie")
	public String movie(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/movie.html";
	}
	@GetMapping(value="/admin/artist")
	public String artist(Model model) {
		this.movieService.artist(model);
		return "admin/artist.html";
	}
	
	@GetMapping(value="/admin/reviews/{id}")
	public String adminReview(@PathVariable("id") Long id,Model model) {
		List<Review> allReviewsOfMovie = this.movieService.adminReview(id,model);
		model.addAttribute("reviews", allReviewsOfMovie);
		return "admin/reviews.html";
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		this.movieService.manageMovies(model);
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		
		this.movieService.setDirectorToFilm(directorId, movieId, model);
		return "admin/formUpdateMovie.html";
	}
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		this.movieService.addDirector(id, model);
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, @RequestParam("file")MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
		
		  if (file.isEmpty()) {
	            System.out.println("Please select a file to upload");
	            return "admin/formNewMovie.html";
	        }
		
		  movie.setImage(file.getBytes());

		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.movieRepository.save(movie);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "movie.html";
	}

	@GetMapping("/movies")
	public String getMovies(Model model) {		
		model.addAttribute("movies", this.movieRepository.findAll());
		return "movies.html";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieRepository.findByYear(year));
		return "foundMovies.html";
	}


	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {
		this.movieService.updateActors(id, model);
		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		this.movieService.addActorsToMovie(actorId, movieId, model);
		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		this.movieService.removeActorFromMovie(actorId, movieId, model);
		return "admin/actorsToAdd.html";
	}

	@GetMapping("/admin/removeReviewFromMovie/{idm}/{idr}")
	public String removeReviewFromMovie(@PathVariable("idm")Long idm, @PathVariable("idr")Long idr, Model model)	{
		List<Review> allReviewsOfMovie = this.movieService.removeReviewFromMovie(idm, idr, model);
		model.addAttribute("reviews", allReviewsOfMovie);
		return "admin/reviews.html";
	}


}
