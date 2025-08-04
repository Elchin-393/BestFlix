package com.bestflix.movie.repository;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.entity.UsersMovie;
import com.bestflix.movie.security.dto.ResetPasswordRequest;
import com.bestflix.movie.security.entity.PasswordResetToken;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.PasswordResetTokenRepository;
import com.bestflix.movie.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class MovieRepositoryTest {


    @Autowired
    private UsersMovieRepository usersMovieRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    UsersMovie usersMovie;
    Users user;
    Movie movie;
    PasswordResetToken passwordResetToken;

    @BeforeEach
    public void setup() {
        user = Users.builder()
                .username("Elcin")
                .email("Mamedovelchin@gmail.com")
                .password("111111")
                .build();

        user = userRepository.save(user); // <-- Save user first

        movie = Movie.builder()
                .movieName("Room")
                .country("Tailand")
                .releaseDate(LocalDate.now())
                .casts("Jenifer Lawrence")
                .duration("230 min")
                .about("Very Interesting Story")
                .category("Movie")
                .imageName("ifie-3242dss-ssdfs")
                .imageType("jpeg")
                .videoName("jewrm3-3453r34-34f")
                .videoType("MP4")
                .imageData("Success".getBytes())
                .videoPath("C:/Users/User/Desktop/BestFlix/video")
                .build();

        movie = movieRepository.save(movie); // <-- Save movie first

        usersMovie = UsersMovie.builder()
                .user(user)
                .movie(movie)
                .build();

        usersMovieRepository.save(usersMovie);

        passwordResetToken= PasswordResetToken.builder()
                .token("jfnjdnkdvsgd")
                .user(user)
                .expiry(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
    }






    //UsersMovieRepo

    @Test
    public void test_Find_By_User_Username(){
        List<UsersMovie> myMovies = usersMovieRepository.findByUserUsername("Elcin");


        assertNotNull(myMovies);
        assertEquals(1, myMovies.size());

        UsersMovie fetched = myMovies.get(0);
        assertEquals("Room", fetched.getMovie().getMovieName());
        assertEquals("Elcin", fetched.getUser().getUsername());
        assertEquals("Tailand", fetched.getMovie().getCountry());
    }

    @Test
    public void test_Find_By_User_Username_By_Invalid_Name(){
        List<UsersMovie> myMovies = usersMovieRepository.findByUserUsername("Enisa");


        assertTrue(myMovies.isEmpty());
    }

    @Test
    public void test_Delete_All_By_Movie_ID(){
        List<UsersMovie> myMovies = usersMovieRepository.findAll();
        assertEquals(1, myMovies.size());

        Long id = myMovies.get(0).getId();

        usersMovieRepository.deleteAllByMovieId(id);

        List<UsersMovie> afterdelete = usersMovieRepository.findAll();

        assertTrue(usersMovieRepository.findAll().isEmpty());
        assertEquals(0, afterdelete.size());


        System.out.println("Movie size " + afterdelete.size());
    }

    @Test
    public void test_Find_By_Movie_Id(){
        Long userId = userRepository.findByUsername("Elcin").getId();
        UsersMovie usersMovie = usersMovieRepository.findByMovieId(userId);


        assertNotNull(usersMovie);
        assertNotNull(usersMovie.getUser());
        assertNotNull(usersMovie.getMovie());


        assertEquals("Elcin", usersMovie.getUser().getUsername());
        assertEquals("Room", usersMovie.getMovie().getMovieName());
    }


    //UserRepository

    @Test
    public void test_Find_By_Username(){
        Users user = userRepository.findByUsername("Elcin");

        assertNotNull(user);

        assertEquals("Elcin", user.getUsername());
        assertEquals("111111", user.getPassword());
        assertEquals("Mamedovelchin@gmail.com", user.getEmail());

    }

    @Test
    public void test_Find_By_Email(){
        Optional<Users> user = userRepository.findByEmail("Mamedovelchin@gmail.com");

        assertNotNull(user);

        assertEquals("Elcin", user.get().getUsername());
        assertEquals("111111", user.get().getPassword());
        assertEquals("Mamedovelchin@gmail.com", user.get().getEmail());

    }


    //PasswordResetTokenRepo

    @Test
    public void test_Find_By_Token(){
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken("jfnjdnkdvsgd");

        assertTrue(!passwordResetToken.isEmpty());

        assertEquals("jfnjdnkdvsgd", passwordResetToken.get().getToken());
        assertEquals("Elcin", passwordResetToken.get().getUser().getUsername());
        assertEquals("111111", passwordResetToken.get().getUser().getPassword());
        assertEquals("Mamedovelchin@gmail.com", passwordResetToken.get().getUser().getEmail());

    }


}
