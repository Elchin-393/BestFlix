/**
 * All Movies Page
 * Loads all movies, supports pagination, and handles user navigation.
 * Uses fetch API and SweetAlert2 for error handling.
 */

/** @type {HTMLElement} */
const sign = document.querySelector(".sign-in");

/**
 * Redirects user to login page when sign-in button is clicked.
 */
sign.addEventListener("click", () => {
  window.location.href = "login.html";
});

/** @type {boolean} */
let searchActive = false;


  document.addEventListener("DOMContentLoaded", async () => {
  try {
    /**
     * Fetches all movies from the backend.
     * @returns {Promise<Object[]>} Array of movie objects
     */
    const response = await fetch("http://localhost:8080/rest/api/movie/all");
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const movies = await response.json();
    const moviesContainer = document.querySelector(".movies");
    const itemsPerPage = 24;
    const totalPages = Math.ceil(movies.length / itemsPerPage);

    const paginationContainer = document.querySelector("#pagination");


    /**
     * Displays movies for the given page.
     * @param {number} page - Page number to display
     */
    function displayMovies(page) {
      moviesContainer.innerHTML = "";

      let start = (page - 1) * itemsPerPage;
      let end = start + itemsPerPage;
      let paginatedMovies = movies.slice(start, end);

      paginatedMovies.forEach(movie => {
        const movieDiv = document.createElement("div");
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
      });
    }


    /**
     * Updates pagination buttons based on current page.
     */
    function updatePagination() {
      paginationContainer.querySelectorAll(".line").forEach(btn => btn.remove());

      let startPage = Math.max(1, currentPage - 1);
      let endPage = Math.min(totalPages, startPage + 2);

      if (endPage - startPage < 2) {
        startPage = Math.max(1, endPage - 2);
      }

      for (let i = startPage; i <= endPage; i++) {
        const btn = document.createElement("button");
        btn.className = "line";
        btn.dataset.page = i;
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");
        
        paginationContainer.insertBefore(btn, document.querySelector("#nextPage"));
      }
    }



    /** @type {URLSearchParams} */
    const urlParams = new URLSearchParams(window.location.search);
    /** @type {string|null} */
    const query = urlParams.get("query")?.toLowerCase().trim();



    /**
     * Updates browser URL with current page without reloading.
     * @param {number} page
     */
    function updateUrlPage(page) {
          const newUrl = `${window.location.pathname}?page=${page}`;
          history.pushState({ page }, "", newUrl);
        }


   /**
     * Handles pagination button clicks.
     */
    paginationContainer.addEventListener("click", (e) => {
      if (e.target.classList.contains("line")) {
        if (searchActive) return;

    currentPage = Number(e.target.dataset.page);
    displayMovies(currentPage);
    updatePagination();
    updateUrlPage(currentPage);
  }
});


/**
* Handles previous page button click.
 */
document.querySelector("#prevPage").addEventListener("click", () => {
  if (searchActive) return;

  if (currentPage > 1) {
    currentPage--;
    displayMovies(currentPage);
    updatePagination();
    updateUrlPage(currentPage);
  }
});


/**
* Handles next page button click.
*/
document.querySelector("#nextPage").addEventListener("click", () => {
  if (searchActive) return;

  if (currentPage < totalPages) {
    currentPage++;
    displayMovies(currentPage);
    updatePagination();
    updateUrlPage(currentPage);
  }
});

        /**
        * Handles browser back/forward navigation.
        */
        window.addEventListener("popstate", () => {
        const urlParams = new URLSearchParams(window.location.search);
        currentPage = parseInt(urlParams.get("page")) || 1;
        displayMovies(currentPage);
        updatePagination();
      });

    /** @type {number} */
    let currentPage = parseInt(urlParams.get("page")) || 1;
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


