
/**
 * Custom Search Engine
 * Handles autocomplete suggestions, paginated movie results,
 * and search routing logic for movies and TV shows.
 */
document.addEventListener("DOMContentLoaded", async () => {
  const apiUrl = "https://bestflix-budz.onrender.com";
  try {
    /** @type {Response} */
    const response = await fetch(`${apiUrl}/rest/api/movie/all`);
    if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

    /** @type {Object[]} */
    const allMovies = await response.json();

    /** @type {HTMLElement} */
    const moviesContainer = document.querySelector(".movies");
    const paginationContainer = document.querySelector("#pagination");
    const searchInput = document.querySelector(".search");
    const searchButton = document.querySelector(".search-button");
    const autocompleteBox = document.querySelector(".autocomplete-results");

        let filteredMovies = [];
        let currentPage = parseInt(new URLSearchParams(window.location.search).get("page")) || 1;

        const itemsPerPage = 24;

        // Setup search listeners
        if (searchButton && searchInput) {
          /**
       * Handles search button click and redirects to results page.
       */
          searchButton.addEventListener("click", () => {
        const term = searchInput.value.trim();
        const path = window.location.pathname.includes("tvshows.html");
        if(path) {
              window.location.href = `resultPage.html?category=tvshows&query=${encodeURIComponent(term)}`;
          }else{
              window.location.href = `resultPage.html?query=${encodeURIComponent(term)}`;
            }
            
        });
      
        /**
       * Handles Enter key press inside search input.
       * Triggers same behavior as search button.
       */
        searchInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
          const term = searchInput.value.trim();
          const path = window.location.pathname.includes("tvshows.html");
          if (term !== "") {
            if(path) {
              window.location.href = `resultPage.html?category=tvshows&query=${encodeURIComponent(term)}`;
            }
            else{
              window.location.href = `resultPage.html?query=${encodeURIComponent(term)}`;
            }
            
            }
          }
        });

        /**
       * Handles live input in search field and displays autocomplete results.
       */
        searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value.toLowerCase().trim();
        autocompleteBox.innerHTML = "";

        if (searchTerm === "") {
          autocompleteBox.style.display = "none";
          return;
        }
        
        
        let baseSet = allMovies;

if (window.location.pathname.endsWith("/html/tvshows.html")) {
  baseSet = allMovies.filter(m => m.category === "TV Show");
}

let matches = baseSet.filter(movie =>
  movie.movieName.toLowerCase().includes(searchTerm)
);
            

        const limited = matches.slice(0, 5);

        limited.forEach(movie => {
          const item = document.createElement("div");
          item.className = "autocomplete-item";
          item.innerHTML = `
            <img class="search-image" src="${apiUrl}/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
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
          const term = searchInput.value.trim();
        const path = window.location.pathname.includes("tvshows.html");
        if(path) {
              window.location.href = `resultPage.html?category=tvshows&query=${encodeURIComponent(term)}`;
          }else{
              window.location.href = `resultPage.html?query=${encodeURIComponent(term)}`;
            }
        });

        autocompleteBox.appendChild(viewButton);
        autocompleteBox.style.display = matches.length > 0 ? "block" : "none";
          });
        }

        /** @type {URLSearchParams} */
        const urlParams = new URLSearchParams(window.location.search);
        const query = urlParams.get("query")?.toLowerCase().trim();

       if (query && moviesContainer) {
        filteredMovies = allMovies.filter(movie =>
          movie.movieName.toLowerCase().includes(query)
        );

        
        const category = urlParams.get("category");

        if (category === "tvshows") {
              filteredMovies = allMovies.filter(m => m.category === "TV Show");
              filteredMovies = filteredMovies.filter(movie => movie.movieName.toLowerCase().includes(query));
            }

        const resultTitle = document.querySelector(".all-results");
        if (filteredMovies.length === 0) {
          moviesContainer.innerHTML ="";
          resultTitle.textContent = `Not found movie "${query}"`;
          resultTitle.style.color = "#dddddd";
          resultTitle.style.width = "1000px";
          resultTitle.classList.add("no-line");
          document.querySelector("#pagination").style.display ="none";
          return; 
        }

        /**
       * Updates browser URL with new page number.
       * @param {number} page
       */
        function updateUrlPage(page) {
          const newUrl = `resultPage.html?query=${encodeURIComponent(query)}&page=${page}`;
          history.pushState({ page }, "", newUrl);
        }


        /**
       * Displays current page of filteredMovies.
       * @param {number} page
       */
        function displayMovies(page) {

          const start = (page - 1) * itemsPerPage;
          const end = start + itemsPerPage;
          const paginated = filteredMovies.slice(start, end);

          moviesContainer.innerHTML = "";

          paginated.forEach(movie => {
            const movieDiv = document.createElement("div");
            movieDiv.innerHTML = `
              <a href="movies.html?id=${movie.id}">
                <img class="poster" src="${apiUrl}/rest/api/movie/image/${movie.id}" alt="${movie.movieName}">
                <h5>${movie.movieName}</h5>
                <h5>${new Date(movie.releaseDate).getFullYear()} <span><img src="/images/icons/dot.png" alt=""></span> ${movie.duration}</h5>
              </a>
            `;
            moviesContainer.appendChild(movieDiv);
          });
        }

        if (window.location.pathname.endsWith("/html/tvshows.html")) {
              filteredMovies = allMovies.filter(m => m.category === "TV Show");
            }

            /**
       * Updates pagination button display.
       */
        function updatePagination() {
          const totalPages = Math.ceil(filteredMovies.length / itemsPerPage);
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

        // Pagination button listeners
        paginationContainer.addEventListener("click", (e) => {
          if (e.target.classList.contains("line")) {
            currentPage = Number(e.target.dataset.page);
            updateUrlPage(currentPage);
            displayMovies(currentPage);
            updatePagination();
          }
        });

        document.getElementById("prevPage").addEventListener("click", () => {
          if (currentPage > 1) {
            currentPage--;
            updateUrlPage(currentPage);
            displayMovies(currentPage);
            updatePagination();
          }
        });

        if (window.location.pathname.endsWith("/html/tvshows.html")) {
              filteredMovies = allMovies.filter(m => m.category === "TV Show");
            }

        document.getElementById("nextPage").addEventListener("click", () => {
          const totalPages = Math.ceil(filteredMovies.length / itemsPerPage);
          if (currentPage < totalPages) {
            currentPage++;
            updateUrlPage(currentPage);
            displayMovies(currentPage);
            updatePagination();
          }
        });

        displayMovies(currentPage);
        updatePagination();
        }  

        /**
     * Restores page state from browser history (back/forward navigation).
     */
        window.addEventListener("popstate", () => {
        const urlParams = new URLSearchParams(window.location.search);
        currentPage = parseInt(urlParams.get("page")) || 1;
        displayMovies(currentPage);
        updatePagination();
      });


      /**
     * Hides autocomplete box if clicked outside.
     * @param {MouseEvent} e
     */
     document.addEventListener("click", (e) => {
      if (!autocompleteBox.contains(e.target) && e.target !== searchInput) {
        autocompleteBox.style.display = "none";
      }
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


