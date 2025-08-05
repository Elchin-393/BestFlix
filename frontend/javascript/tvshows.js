
/** @file tvshows.js
 * Renders TV Show listings from the movie API,
 * provides pagination, and handles header navigation.
 */

// Sign-In Button Routing
 const sign = document.querySelector(".sign-in");
 sign.addEventListener("click", function() {

  window.location.href = "login.html";
  
  });

  document.addEventListener("DOMContentLoaded", async () => {

  try {
    /** @type {Response} */
    const response = await fetch("http://localhost:8080/rest/api/movie/all");
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    /** @type {Object[]} */
    const movies = await response.json();
    const moviesContainer = document.querySelector(".movies");

    const itemsPerPage = 24;
    let currentPage = 1;

    /** @type {Object[]} Filters only TV Shows */
    const tvShows = movies.filter(m => m.category === "TV Show");
    const totalPages = Math.ceil(tvShows.length / itemsPerPage);

    const paginationContainer = document.querySelector("#pagination");

    /**
     * Displays TV Shows for the current page.
     * @param {number} page
     */
    function displayMovies(page) {
      moviesContainer.innerHTML = "";

      let start = (page - 1) * itemsPerPage;
      let end = start + itemsPerPage;
      let paginatedMovies = tvShows.slice(start, end);

      paginatedMovies.forEach(movie => {
        const movieDiv = document.createElement("div");
        if(movie.category == "TV Show") {
        movieDiv.innerHTML = `
          <a href="movies.html?id=${movie.id}">
            <img class="poster" src="http://localhost:8080/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
            <h5>${movie.movieName}</h5>
            <h5>${new Date(movie.releaseDate).getFullYear()} 
              <span><img src="/images/icons/dot.png" alt=""></span> 
              ${movie.duration}
            </h5>
          </a>
        `;
        moviesContainer.appendChild(movieDiv);
        }
      });
    }

    const urlParams = new URLSearchParams(window.location.search);
    const query = urlParams.get("query")?.toLowerCase().trim();

    /**
     * Updates browser history URL based on page.
     * @param {number} page
     */
    function updateUrlPage(page) {
          const newUrl = `${window.location.pathname}?page=${page}`;
          history.pushState({ page }, "", newUrl);
        }


    /**
     * Renders pagination buttons and activates current page.
     */    
    function updatePagination() {
      paginationContainer.querySelectorAll(".line").forEach(btn => btn.remove());

      let startPage = Math.max(1, currentPage - 1);
      let endPage = Math.min(totalPages, startPage + 2);

      if (endPage - startPage < 2) {
        startPage = Math.max(1, endPage - 2);
      }


     /**
      * Generates numbered pagination buttons based on current page range.
      * Each button updates the URL, renders relevant TV shows, and refreshes pagination view.
      *
      * Buttons are dynamically created and inserted before the "Next" button.
      *
      * @param {number} startPage - The first page number to render.
      * @param {number} endPage - The last page number to render.
      */
      for (let i = startPage; i <= endPage; i++) {
        const btn = document.createElement("button");
        btn.className = "line";
        btn.dataset.page = i;
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");

        btn.addEventListener("click", () => {
          currentPage = i;
          updateUrlPage(currentPage);
          displayMovies(currentPage);
          updatePagination();
        });
        
        paginationContainer.insertBefore(btn, document.querySelector("#nextPage"));
      }
    }

    document.querySelector("#prevPage").addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        updateUrlPage(currentPage);
        displayMovies(currentPage);
        updatePagination();
      }
    });

    document.querySelector("#nextPage").addEventListener("click", () => {
      if (currentPage < totalPages) {
        currentPage++;
        updateUrlPage(currentPage);
        displayMovies(currentPage);
        updatePagination();
      }
    });

      /**
       * Restores page when user navigates browser history.
       */
      window.addEventListener("popstate", () => {
        const urlParams = new URLSearchParams(window.location.search);
        currentPage = parseInt(urlParams.get("page")) || 1;
        displayMovies(currentPage);
        updatePagination();
      });


    // Initial Page Load   
    displayMovies(currentPage);
    updatePagination();

  } catch (error) {
    console.error("Failed to load movies:", error);
    Swal.fire({
      title: "Oops",
      text: "Could not load movies!. Please try again later!",
      icon: "error"
    });
  }
});


