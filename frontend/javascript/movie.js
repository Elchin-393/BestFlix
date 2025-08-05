/**
 * Movie Page Loader
 * Fetches movie details from server and renders the movie view including a video player.
 */
document.addEventListener("DOMContentLoaded", async () => {

  /** @type {HTMLElement} */
  const moviePage = document.getElementById("movie");

  /** @type {string|null} */
  const movieId = new URLSearchParams(window.location.search).get("id");

  if(!movieId){
    moviePage.innerHTML = "Movie id not found!";
    return;
  }

 try {

  /** @type {Response} */
  const response = await fetch(`http://localhost:8080/rest/api/movie/${movieId}`);

  if(!response.ok){
      throw new Error(`HTTP error! status: ${response.status}`);
  }

  /** @type {Object} */
  const movie = await response.json();
  moviePage.innerHTML = "";

      /** @type {Object} */
      const moviePageDiv = document.createElement("div");
      moviePageDiv.innerHTML = `
      
      <div class="picture"> <img src="http://localhost:8080/rest/api/movie/image/${movieId}"></div>
      <button class="watch-btn">Watch now</button>
      <div><h2>${movie.movieName}</h2></div>
      <div id="info">
          <h4 class="info-1">Released: ${movie.releaseDate}</h4>
          <h4 class="info-1">Country: ${movie.country}</h4>
          <h4 class="info-1">Casts: ${movie.casts}</h4>
          <h4 class="info-1">Duration: ${movie.duration}</h4>
          <p>About: ${movie.about}</p>
        </div>

      `
      moviePage.appendChild(moviePageDiv);

      
      /** @type {Object} */
      const watchBtn = document.querySelector(".watch-btn");

      watchBtn.addEventListener("click", async () =>{

        watchBtn.classList.add("active");
        const movieId = new URLSearchParams(window.location.search).get("id");        

        /** @type {HTMLElement} */
        const videoSection = document.querySelector("#video-section");
        videoSection.style.display = "block";
        videoSection.innerHTML = ""
        try {
        const videoDiv = document.createElement("div");
        videoDiv.innerHTML = `
          <video class="video" width="900" controls>
            <source src="http://localhost:8080/rest/api/movie/video/${movieId}" type="video/mp4">
            Your browser does not support the video tag.
          </video>
        `;

        videoSection.appendChild(videoDiv);

        videoSection.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });

      } catch (error) {
        console.error("Failed to load video:", error);
        videoSection.innerHTML = "<p>Could not load video.</p>";
      }


   })



  } catch (error) {
    console.error("Failed to load movies:", error);
    Swal.fire({
      title: "Oops",
      text: "Could not load movies!. Please try again later!",
      icon: "error"
    });
  };

})
