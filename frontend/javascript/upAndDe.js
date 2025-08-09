/**
 * Movie Update & Delete Logic
 * Handles deletion and redirection for movie update via dataset ID.
 */
document.addEventListener("DOMContentLoaded", async () => {

  const apiUrl = "https://bestflix-budz.onrender.com";

  
  const token = localStorage.getItem("jwtToken");


  /**
   * Handles delete button clicks, confirms intent, and calls delete API.
   */
  document.addEventListener("click", async (e) => {
    const deleteBtn = e.target.closest(".delete");
    if (!deleteBtn) return;

    const movieDiv = deleteBtn.closest("[data-id]");

    if (!movieDiv) {
      Swal.fire({
      title: "Oops",
      text: "Movie Not Found",
      icon: "warning"
    });
    }

    const movieId = movieDiv.dataset.id;

    const confirmed = window.confirm("Are you sure you want to delete this movie?");
    if (!confirmed) return;


    try {
      const response = await fetch(`${apiUrl}/rest/api/movie/delete/${movieId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error(await response.text() || response.statusText);
      }

      movieDiv.remove();
      location.reload(); 
    } catch (error) {
      console.error("Failed to delete movie:", error);
      Swal.fire({
      title: "Oops",
      text: "Something went wrong while deleting the movie.",
      icon: "error"
    });
    }
  });
});

/**
 * Handles update button clicks and redirects to edit screen.
 */
document.addEventListener("DOMContentLoaded", () => {
  document.addEventListener("click", (e) => {
    const updateBtn = e.target.closest(".update");
    if (!updateBtn) return;

    const movieDiv = updateBtn.closest("[data-id]");
    if (!movieDiv) {
      Swal.fire({
      title: "Oops",
      text: "Movie Not Found",
      icon: "warning"
    });
      return;
    }

    const movieId = movieDiv.dataset.id;
    window.location.href = `addmovie.html?id=${movieId}`;
  });
});