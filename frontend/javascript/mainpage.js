 /**
 * Main Page Script
 * Handles redirect on sign-in click and search bar animation.
 */


  // Search input behavior
  document.addEventListener("DOMContentLoaded", () => {
  /** @type {HTMLInputElement} */
  const searchInput = document.querySelector("#search input");
  /** @type {HTMLElement} */
  const searchContainer = document.querySelector("#search");

  /**
   * Adds or removes the 'move-up' class based on input content.
   */
  searchInput.addEventListener("input", () => {
    if (searchInput.value.trim() !== "") {
      searchContainer.classList.add("move-up");
    } else {
      searchContainer.classList.remove("move-up");
    }
  });

});


