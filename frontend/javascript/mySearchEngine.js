/**
 * My Search Engine
 * Implements live search, autocomplete, pagination, and history syncing
 * for a user's personal movie collection.
 */
document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("jwtToken");

  /** @type {{ username?: string, sub?: string }} */
  const payload = JSON.parse(atob(token.split(".")[1]));
  const username = payload.username || payload.sub;

  const itemsPerPage = 16;
  let currentPage = 1;
  let filteredMovies = [];
  let myMovies = [];

  try {

    const urlParams = new URLSearchParams(window.location.search);
    const queryParam = urlParams.get("query")?.toLowerCase() || "";
    const pageParam = parseInt(urlParams.get("page")) || 1;
    currentPage = pageParam;

    const response = await fetch("http://localhost:8080/rest/api/movie/mymovies", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username })
    });

    myMovies = await response.json();

    const moviesContainer = document.querySelector("#my-movie-content .movies");
    const paginationContainer = document.querySelector("#pagination");
    const searchInput = document.querySelector("#my-search .search");
    const searchButton = document.querySelector("#my-search .search-button");
    const autocompleteBox = document.querySelector(".my-autocomplete-results");

    const viewButton = document.createElement("button");
    viewButton.className = "viewButton";
    viewButton.textContent = "View all results >";

    

    /**
     * Renders a paginated set of filtered movies.
     * @param {number} [page=1]
     */
    function displayMovies(page = 1) {
      const start = (page - 1) * itemsPerPage;
      const end = start + itemsPerPage;
      const paginatedMovies = filteredMovies.slice(start, end);

      moviesContainer.innerHTML = "";

      
      if (paginatedMovies.length === 0) {
        const notFoundMsg = document.createElement("h2");
        notFoundMsg.textContent = `Not found movie "${searchInput.value}"`;
        notFoundMsg.style.color = "#dddddd";
        notFoundMsg.style.width = "1000px";
        moviesContainer.appendChild(notFoundMsg);
        paginationContainer.style.display = "none";
        return;
      }
      paginationContainer.style.display = "flex";
      
      

      paginatedMovies.forEach(movie => {
        const movieDiv = document.createElement("div");
        movieDiv.classList.add("data-id");
        movieDiv.dataset.id = movie.id;
        movieDiv.innerHTML = `
          <a href="movies.html?id=${movie.id}">
            <img class="poster" src="http://localhost:8080/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
            <h5>${movie.movieName}</h5>
            <h5>${new Date(movie.releaseDate).getFullYear()} <span><img src="/images/icons/dot.png" alt=""></span> ${movie.duration}</h5>
          </a>
          <div id="up-and-de">
              <button class="update"><h5>Update</h5></button>
              <button class="delete"><h5>Delete</h5></button>
            </div>
        `;
        moviesContainer.appendChild(movieDiv);
      });
    }

    /**
     * Updates pagination controls for current search.
     */
    function updatePagination() {
      const totalPages = Math.max(1, Math.ceil(filteredMovies.length / itemsPerPage));

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

        btn.addEventListener("click", () => {
  const term = searchInput.value.trim().toLowerCase();
  performSearch(term, parseInt(btn.dataset.page));
});

        paginationContainer.insertBefore(btn, document.querySelector("#nextPage"));
      }

      document.querySelector("#prevPage").disabled = currentPage === 1;
      document.querySelector("#nextPage").disabled = currentPage === totalPages;
    }

    // Pagination nav handlers
    document.querySelector("#prevPage").onclick = () => {
  if (currentPage > 1) {
    const term = searchInput.value.trim().toLowerCase();
    performSearch(term, currentPage - 1);
  }
};

document.querySelector("#nextPage").onclick = () => {
  const totalPages = Math.ceil(filteredMovies.length / itemsPerPage);
  if (currentPage < totalPages) {
    const term = searchInput.value.trim().toLowerCase();
    performSearch(term, currentPage + 1);
  }
};


  /**
  * Filters movies by search term and updates page state.
  * @param {string} searchTerm
  * @param {number} [page=1]
  * @param {boolean} [push=true]
  */
  function performSearch(searchTerm, page = 1, push = true) {
  const term = searchTerm.toLowerCase();
  filteredMovies = myMovies.filter(movie =>
    movie.movieName.toLowerCase().includes(term)
  );
  currentPage = page;

  if (push && searchTerm !== "") {
    const params = new URLSearchParams({ query: term, page: currentPage });
    history.pushState({ query: term, page: currentPage }, "", `?${params}`);
  } else if(push && searchTerm === ""){
    const params = new URLSearchParams({page: currentPage });
    history.pushState({page: currentPage }, "", `?${params}`);
  }

  displayMovies(currentPage);
  updatePagination();
}

    /**
     * Displays autocomplete results below search input.
     */
    function handleAutocomplete() {
      autocompleteBox.innerHTML = "";
      const searchTerm = searchInput.value.toLowerCase().trim();

      if (!searchTerm) {
        autocompleteBox.style.display = "none";
        searchInput.style.borderRadius = "100px";
        return;
      }

      searchInput.style.borderRadius = "7px 7px 0 0";

      const matches = myMovies.filter(movie =>
        movie.movieName.toLowerCase().includes(searchTerm)
      );

      matches.slice(0, 5).forEach(movie => {
        const item = document.createElement("div");
        item.className = "autocomplete-item";
        item.innerHTML = `
          <img class="search-image" src="http://localhost:8080/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
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

      viewButton.onclick = () => {
        performSearch(searchTerm);
        autocompleteBox.style.display = "none";
      };

      autocompleteBox.appendChild(viewButton);
      autocompleteBox.style.display = matches.length > 0 ? "block" : "none";
    }

    // Search input listeners
    searchInput.addEventListener("input", handleAutocomplete);

    searchInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        const term = searchInput.value.trim().toLowerCase();
        performSearch(term);
        autocompleteBox.style.display = "none";
        
        
      }
    });

    searchButton.addEventListener("click", () => {
      const term = searchInput.value.trim().toLowerCase();
      if (term) {
        performSearch(term);
        autocompleteBox.style.display = "none";
      }
    });

    document.addEventListener("click", (e) => {
      if (!autocompleteBox.contains(e.target) && e.target !== searchInput) {
        autocompleteBox.style.display = "none";
      }
    });

    // Initial load
    // Initial load from URL params
searchInput.value = queryParam;
performSearch(queryParam, pageParam, false); // load, don’t push history again


    window.addEventListener("popstate", (event) => {
  const state = event.state || {};
  const query = state.query || "";
  const page = state.page || 1;
  searchInput.value = query;
  performSearch(query, page, false); // don't push again!
});

  } catch (error) {
    console.error("Failed to load movies:", error);
    Swal.fire({
      title: "Oops",
      text: "Could not load movies!. Please try again later!",
      icon: "error"
    });
  }
});

