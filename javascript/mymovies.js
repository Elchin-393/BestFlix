/**
 * My Movies Page
 * Fetches user's movie collection using JWT and enables pagination, display, and logout logic.
 */
document.addEventListener("DOMContentLoaded", async () => {
  const add = document.querySelector("#plus .add");
  add.addEventListener("click", () => window.location.href = "addmovie.html");

  const itemsPerPage = 16;
  let currentPage = parseInt(new URLSearchParams(window.location.search).get("page")) || 1;

  const token = localStorage.getItem("jwtToken");

  /** @type {{ username?: string, sub?: string }} */
  const payload = JSON.parse(atob(token.split(".")[1]));
  const username = payload.username || payload.sub;

  const moviesContainer = document.querySelector("#my-movie-content .movies");
  const paginationContainer = document.querySelector("#pagination");

  try {
    /** @type {Response} */
    const response = await fetch("http://localhost:8080/rest/api/movie/mymovies", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username })
    });

    if (!response.ok) {
      throw new Error(await response.text() || response.statusText);
    }

    /** @type {Object[]} */
    const myMovies = await response.json();
    const totalPages = Math.ceil(myMovies.length / itemsPerPage);

    const urlParams = new URLSearchParams(window.location.search);

    /** @type {string|null} */
    const searchQuery = urlParams.get("query")?.toLowerCase();


    /**
     * Renders movies on the page based on the current pagination.
     * @param {number} page
     */
    function displayMovies(page) {
      if (searchQuery) return;
       moviesContainer.innerHTML = "";

      const start = (page - 1) * itemsPerPage;
      const paginated = myMovies.slice(start, start + itemsPerPage);

      paginated.forEach(movie => {
        const movieDiv = document.createElement("div");
        movieDiv.classList.add("data-id")
        movieDiv.dataset.id = movie.id;
        movieDiv.innerHTML = `
          <a href="movies.html?id=${movie.id}">
            <img class="poster" src="http://localhost:8080/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
            <h5 class="movie-name">${movie.movieName}</h5>
            <h5>${new Date(movie.releaseDate).getFullYear()}
              <span><img src="/images/icons/dot.png" alt=""></span>
              ${movie.duration}
            </h5>
          </a>
          <div id="up-and-de">
              <button class="update"><h5>Update</h5></button>
              <button class="delete"><h5>Delete</h5></button>
            </div>
        `;
        moviesContainer.appendChild(movieDiv);
      });

      document.querySelector("#log-out h2").textContent = username;
    }
    

    
    /**
     * Updates pagination button states.
     */
    function updatePagination() {
      if (searchQuery) {
        paginationContainer.style.display = "none";
        return;
      }

      paginationContainer.style.display = "flex";
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
          currentPage = i;
          updateUrlPage(currentPage);
          displayMovies(currentPage);
          updatePagination();
        });

        paginationContainer.insertBefore(btn, document.querySelector("#nextPage"));
      }
    }

    /**
     * Updates the URL to reflect the current page.
     * @param {number} page
     */
    function updateUrlPage(page) {
      const newUrl = `${window.location.pathname}?page=${page}`;
      history.pushState({ page }, "", newUrl);
    }



    

    document.querySelector("#prevPage").addEventListener("click", () => {
      if (searchQuery) return;
      if (currentPage > 1) {
        currentPage--;
        displayMovies(currentPage);
        updatePagination();
      }
    });

    document.querySelector("#nextPage").addEventListener("click", () => {
      if (searchQuery) return;
      if (currentPage < totalPages) {
        currentPage++;
        displayMovies(currentPage);
        updatePagination();
      }
    });

    window.addEventListener("popstate", () => {
      const stateParams = new URLSearchParams(window.location.search);
      currentPage = parseInt(stateParams.get("page")) || 1;
      const query = stateParams.get("query")?.toLowerCase();

      if (query) {
        paginationContainer.style.display = "none";
      } else {
        displayMovies(currentPage);
        updatePagination();
      }
    });

    if (!searchQuery) {
      displayMovies(currentPage);
      updatePagination();
    } else {
      paginationContainer.style.display = "none";
    }

    document.querySelector(".log-out-btn").addEventListener("click", () => {
      localStorage.removeItem("jwtToken");
      window.location.href = "login.html";
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