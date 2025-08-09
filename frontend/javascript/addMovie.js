
/**
 * Handles upload and update operations for movies.
 * Validates image and video inputs, movie metadata, and updates UI accordingly.
 * Relies on SweetAlert2 and fetch API for communication.
 */
document.querySelector(".upload-btn").addEventListener("click", async () => {
  const apiUrl = "https://bestflix-budz.onrender.com";


  /** @type {string} */
  const uploadLabel = document.querySelector(".upload-btn h5")?.textContent;
  const isUpdate = uploadLabel === "Update";

  /** @type {File} */
  const image = document.getElementById("picture-upload").files[0];
  /** @type {File} */
  const video = document.getElementById("video-upload").files[0];
  if (!image || !video) {
    Swal.fire({
    title: "Oops",
    text: "Please select both image and video",
    icon: "warning"
  });
    return;
  }

  /** @type {string} */
  const imageURL = URL.createObjectURL(image);
  const img = new Image();
  img.src = imageURL;

  await img.decode(); 

  const actualWidth = img.width;
  const actualHeight = img.height;

  const desiredWidth = 200;
  const desiredHeight = 300;
  const aspectRatioMatches = Math.abs(actualWidth / actualHeight - desiredWidth / desiredHeight) < 0.05;

  URL.revokeObjectURL(img.src); 

  if (!aspectRatioMatches) {
    Swal.fire({
    title: "Oops",
    text: `Image dimensions: ${actualWidth}×${actualHeight}. Suggested size is ${desiredWidth}×${desiredHeight}px. Your image may appear stretched or distorted.`,
    icon: "warning"
  });
      return;
  }

  /** @type {FormData} */
  const formData = new FormData();
  const token = localStorage.getItem("jwtToken");

  /** @type {{movieName: string, country: string, releaseDate: string, casts: string, duration: string, about: string, category: string}} */
  const movieData = {
    movieName: document.getElementById("movieName").value,
    country: document.getElementById("country").value,
    releaseDate: document.getElementById("releaseDate").value,
    casts: document.getElementById("casts").value,
    duration: document.getElementById("duration").value,
    about: document.getElementById("about").value,
    category: document.getElementById("selection").value
  };

    formData.append("movie", new Blob([JSON.stringify(movieData)], { type: "application/json" }));


  formData.append("image", image);
  formData.append("video", video);

  const payload = JSON.parse(atob(token.split(".")[1]));
  const username = payload.username || payload.sub;
  formData.append("username", username);
  
if (isUpdate) {
    const movieId = new URLSearchParams(window.location.search).get("id");
    formData.append("movieId", movieId);
  }

  const uploadBtn = document.querySelector(".upload-btn");
  uploadBtn.disabled = true;
  uploadBtn.style.opacity = "0.5";
  if (isUpdate) {
    uploadBtn.querySelector("h5").textContent = "Updating...";
  } else{
  uploadBtn.querySelector("h5").textContent = "Uploading...";
  }
  document.body.style.pointerEvents = "none"; 

  try {
    /** @type {string} */
    const url = isUpdate
      ? `${apiUrl}/rest/api/movie/update`
      : `${apiUrl}/rest/api/movie/upload`;
      

      /** @type {Response} */
    const response = await fetch(url, {
      method: isUpdate? "PUT" : "POST",
      body: formData
    });

    if (!response.ok) {
      throw new Error(await response.text() || response.statusText);
    }

    const result = await response.text();
    alert(result);

    
    
  } catch (error) {
    console.error("Upload failed", error);
    

  } finally {
    uploadBtn.disabled = false;
    uploadBtn.style.opacity = "1";
    uploadBtn.querySelector("h5").textContent = isUpdate ? "Update" : "Upload";
    document.body.style.pointerEvents = "auto";
    }


  let duration = document.getElementById("duration").value;

  if(duration.length !== 7 && duration.length !== 6){
     Swal.fire({
      title: "Oops",
      text: "Duration was entered wrong!",
      icon: "warning"
    });
  }

  });


  /**
 * Updates image input placeholder with selected file name.
 */
  document.getElementById("picture-upload")
  .addEventListener("change", function() {
    const addPicture = document.querySelector(".input-picture");

    if(this.files.length > 0) {
      addPicture.placeholder = this.files[this.files.length - 1].name;
    }
    else{
      addPicture.placeholder = "Add Picture";
    }

});

/**
 * Updates video input placeholder with selected file name.
 */
document.getElementById("video-upload")
.addEventListener("change", function() {
  const addPicture = document.querySelector(".input-movie");

  if(this.files.length > 0) {
    addPicture.placeholder = this.files[this.files.length - 1].name;
  }
  else{
    addPicture.placeholder = "Add Movie";
  }
});




/**
 * Loads movie data into form fields for update operation on DOM load.
 */
document.addEventListener("DOMContentLoaded", async () => {

    const apiUrl = "https://bestflix-budz.onrender.com";

  const params = new URLSearchParams(window.location.search);
  const movieId = params.get("id");
  if (!movieId) return;

  try {
    const token = localStorage.getItem("jwtToken");

    const response = await fetch(`${apiUrl}/rest/api/movie/${movieId}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!response.ok) throw new Error("Failed to load movie");

    const movie = await response.json();

    document.getElementById("movieName").value = movie.movieName;
    document.getElementById("country").value = movie.country;
    document.getElementById("releaseDate").value = movie.releaseDate;
    document.getElementById("casts").value = movie.casts;
    document.getElementById("duration").value = movie.duration;
    document.getElementById("about").value = movie.about;
    document.getElementById("selection").value = movie.category;
    
    const uploadBtn = document.querySelector(".upload-btn h5");
    if (uploadBtn) uploadBtn.textContent = "Update";

    

  } catch (error) {
    console.error("Movie load error:", error);
    Swal.fire({
      title: "Oops",
      text: "Could not load movie for update!",
      icon: "error"
    });
  }
});


