/**
 * Search Engine
 * Dynamically loads and filters movies, handles autocomplete suggestions,
 * and manages pagination state across both movie and TV show views.
 */
document.addEventListener("DOMContentLoaded", async () => {

  const apiUrl = "http://localhost:8080";

  try {
    /** @type {Response} */
    const response = await fetch(`${apiUrl}/rest/api/movie/all`);
    if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

    /** @type {Object[]} */
    const allFetchedMovies = await response.json();
    const isTVShowsPage = document.querySelector(".TV-line") !== null;

    /** @type {Object[]} */
    const allMovies = isTVShowsPage
      ? allFetchedMovies.filter(movie => movie.category === "TV Show")
      : allFetchedMovies;

    let filteredMovies = [];
    let searchActive = false;
    const urlParams = new URLSearchParams(window.location.search);
    let currentPage = parseInt(urlParams.get("page")) || 1;

    const itemsPerPage = 24;

    const moviesContainer = document.querySelector(".movies");
    const paginationContainer = document.querySelector("#pagination");
    const searchInput = document.querySelector(".search");
    const searchButton = document.querySelector(".search-button");
    const autocompleteBox = document.querySelector(".autocomplete-results");


    /**
     * Renders movies based on current page and search mode.
     * @param {number} page
     */
    function displayMovies(page) {
      const movieList = searchActive ? filteredMovies : allMovies;
      moviesContainer.innerHTML = "";

      const start = (page - 1) * itemsPerPage;
      const end = start + itemsPerPage;
      const paginated = movieList.slice(start, end);

      paginated.forEach(movie => {
        const movieDiv = document.createElement("div");
        movieDiv.innerHTML = `
          <a href="movies.html?id=${movie.id}">
            <img class="poster" src="${apiUrl}/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
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
     * Updates pagination controls and highlights current page.
     */
    function updatePagination() {
      const movieList = searchActive ? filteredMovies : allMovies;
      const totalPages = Math.ceil(movieList.length / itemsPerPage);

      paginationContainer.querySelectorAll(".line").forEach(btn => btn.remove());

      let startPage = Math.max(1, currentPage - 1);
      let endPage = Math.min(totalPages, startPage + 2);
      if (endPage - startPage < 2) startPage = Math.max(1, endPage - 2);

      for (let i = startPage; i <= endPage; i++) {
        const btn = document.createElement("button");
        btn.className = "line";
        btn.dataset.page = i;
        btn.textContent = i;
        if (i === currentPage) btn.classList.add("active");
        paginationContainer.insertBefore(btn, document.querySelector("#nextPage"));
      }
    }

    /**
     * Executes a filtered search and updates display + pagination.
     * @param {string} term
     */
    function handleSearch(term) {
      const searchTerm = term.toLowerCase().trim();
      filteredMovies = allMovies.filter(movie =>
        movie.movieName.toLowerCase().includes(searchTerm)
      );

      searchActive = true;
      currentPage = 1;
      displayMovies(currentPage);
      updatePagination();

      const title = document.querySelector("#movie-content h1");
      if (title) title.textContent = "All Results";
    }

    // Search listeners
    searchButton.addEventListener("click", () => {
      autocompleteBox.style.display = "none";
      handleSearch(searchInput.value);
    });

    searchInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        autocompleteBox.style.display = "none";
        handleSearch(searchInput.value);
      }
    });


    /**
     * Generates autocomplete box suggestions based on input text.
     */
    searchInput.addEventListener("input", () => {
      const searchTerm = searchInput.value.toLowerCase().trim();
      autocompleteBox.innerHTML = "";

      if (searchTerm === "") {
        autocompleteBox.style.display = "none";
        return;
      }

      const matches = allMovies.filter(movie =>
        movie.movieName.toLowerCase().includes(searchTerm)
      );

      const limited = matches.slice(0, 5);

      limited.forEach(movie => {
        const item = document.createElement("div");
        item.className = "autocomplete-item";
        item.innerHTML = `
          <img src="${apiUrl}/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
          <div id="movie-info">
            <div><span class="movie-name">${movie.movieName}</span></div>
            <div class="bottom-info">
              <span>${new Date(movie.releaseDate).getFullYear()}</span>
              <span class="dot"><img src="/images/icons/black-dot.png" alt=""></span>
              <span>${movie.country}</span>
              <span class="dot"><img src="/images/icons/black-dot.png" alt=""></span>
              <span>${movie.category}</span>
            </div>
          </div>
        `;
        item.addEventListener("click", () => {
          window.location.href = `movies.html?id=${movie.id}`;
        });
        autocompleteBox.appendChild(item);
      });

      const viewButton = document.createElement("button");
      viewButton.className = "viewButton";
      viewButton.textContent = "View all results >";
      viewButton.addEventListener("click", () => {
        autocompleteBox.style.display = "none";
        handleSearch(searchTerm);
      });

      autocompleteBox.appendChild(viewButton);
      autocompleteBox.style.display = matches.length > 0 ? "block" : "none";
    });

    // Pagination listeners
    paginationContainer.addEventListener("click", (e) => {
      if (e.target.classList.contains("line")) {
        currentPage = Number(e.target.dataset.page);
        const newUrl = `${window.location.pathname}?page=${currentPage}`;
        history.pushState({ page: currentPage }, "", newUrl);
        displayMovies(currentPage);
        updatePagination();
      }
    });

    document.querySelector("#prevPage").addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        history.pushState({ page: currentPage }, "", newUrl);
        displayMovies(currentPage);
        updatePagination();
      }
    });

    document.querySelector("#nextPage").addEventListener("click", () => {
      const movieList = searchActive ? filteredMovies : allMovies;
      const totalPages = Math.ceil(movieList.length / itemsPerPage);

      if (currentPage < totalPages) {
        currentPage++;
        history.pushState({ page: currentPage }, "", newUrl);
        displayMovies(currentPage);
        updatePagination();
      }
    });

    window.addEventListener("popstate", (event) => {
  const urlParams = new URLSearchParams(window.location.search);
  currentPage = parseInt(urlParams.get("page")) || 1;
  displayMovies(currentPage);
  updatePagination();
});



    /**
     * Hides autocomplete when user clicks outside.
     * @param {MouseEvent} e
     */
    document.addEventListener("click", (e) => {
      if (!autocompleteBox.contains(e.target) && e.target !== searchInput) {
        autocompleteBox.style.display = "none";
        
      }
    });


    // Initial render
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