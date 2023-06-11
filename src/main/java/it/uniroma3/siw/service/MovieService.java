package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;


@Service
public class MovieService {
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Transactional
	public List<Review> adminReview(Long id, Model model)	{
		Movie movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		List<Review> allReviewsOfMovie = this.allReviewsOfFilm(movie);
		return allReviewsOfMovie;
	}
	
	@Transactional
	public void manageMovies(Model model)	{
		model.addAttribute("movies", this.movieRepository.findAll());
		return;
	}
	
	@Transactional
	public void setDirectorToFilm(Long directorId,Long idm, Model model)	{
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.movieRepository.findById(idm).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		
		model.addAttribute("movie", movie);
		return;
	}
	
	@Transactional
	public void addDirector(Long id, Model model)	{
		model.addAttribute("artists", artistRepository.findAll());
		model.addAttribute("movie", movieRepository.findById(id).get());
		return;
	}
	
	@Transactional
	public void updateActors(Long id, Model model)	{
		List<Artist> actorsToAdd = this.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return;
	}
	
	@Transactional
	public void addActorsToMovie(Long actorId, Long movieId, Model model)	{
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieRepository.save(movie);
		
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);
	}
	
	@Transactional
	public void removeActorFromMovie(Long actorId, Long movieId, Model model)	{
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieRepository.save(movie);

		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);
	}
	
	@Transactional
	public List<Review> removeReviewFromMovie(Long idm, Long idr, Model model)	{
		Movie movie = this.movieRepository.findById(idm).get();
		model.addAttribute("movie", movie);
		Review review = this.reviewRepository.findById(idr).get();
		model.addAttribute("review", review);
		movie.getMovieReviews().remove(review);
		review.setMovieReviewed(null);
		review.setReviewer(null);
		User u = this.getCurrentUser();
		u.getWrittenReviews().remove(review);
		this.reviewRepository.delete(review);
		List<Review> allReviewsOfMovie = this.allReviewsOfFilm(movie);

		model.addAttribute("reviews", allReviewsOfMovie);
		return allReviewsOfMovie;
	}
	
	@Transactional
	public void artist(Model model)	{
		model.addAttribute("artists", this.artistRepository.findAll());
		return;
	}
	
	public List<Review> allReviewsOfFilm(Movie movie)	{
		List<Review> allReviewsOfMovie = new ArrayList<>();
		for(Review r : this.reviewRepository.findAll())	{
			if(r.getMovieReviewed().equals(movie))	{
				allReviewsOfMovie.add(r);
			}
		}
		return allReviewsOfMovie;
	}
	
	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}
	
	public User getCurrentUser()	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		User u = credentials.getUser();
		return u;
	}
}
