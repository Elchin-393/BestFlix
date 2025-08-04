/**
 * Handles click event on the "Register" link.
 * Navigates the user to the registration page.
 */
const register = document.querySelector("#register a");
register.addEventListener("click", () => {
  window.location.href = "register.html";
});

/**
 * Handles click event on the "Forgot Password" link.
 * Navigates the user to the password recovery page.
 */
const forgotPassword = document.querySelector("#remember a");
forgotPassword.addEventListener("click", () => {
  window.location.href = "forgot-password.html";
});


/**
 * Initializes login functionality after DOM content is loaded.
 */
document.addEventListener("DOMContentLoaded", async() =>{

  const loginBtn = document.querySelector(".log-btn");

  /**
   * Handles login button click, performs input validation and API request.
   * @param {MouseEvent} e - The click event on the login button
   */
  loginBtn.addEventListener("click", async(e)=> {
    e.preventDefault()

    try{

    const username = document.querySelector(".input-u").value.trim();
    const password = document.querySelector(".input-p").value.trim();

  
    // Input validation checks
  if(!username || !password){
     Swal.fire({
      title: "Oops",
      text: "Please enter username and password!",
      icon: "warning"
    });
    return;
  }

  if (!username || username.length < 3) {
    Swal.fire({
      title: "Oops",
      text: "Username must be at least 3 characters long!",
      icon: "warning"
    });
    return;
  }

  if (!password || password.length < 6) {
    Swal.fire({
    icon: 'warning',
    title: 'Invalid Input',
    text: 'Please enter a password with at least 6 characters.'
  });

    return;
  }

  const formData = new URLSearchParams();
  formData.append("username", username);
  formData.append("password",password);

  const data = {username, password};

  
  /**
  * Sends login request to backend.
  * @returns {Response} response containing JWT or error details
  */
  const response = await fetch("http://localhost:8080/login", {
    method: "POST",
    headers: {"Content-Type" : "application/json"},
    body: JSON.stringify(data)
  })

  if (!response.ok) {
        const errorData = await response.json();
        const message = errorData?.errorDetails?.message || "Unknown error";
        Swal.fire({
        title: "Oops",
        text: message,
        icon: "error"
      }); 
        return;      
      }


      const token = await response.text();

      
      localStorage.setItem("jwtToken", token);

      await Swal.fire({
      title: "Good Job!",
      text: "You logged in successfully",
      icon: "success"
    });

      
    window.location.href = "mymovies.html";

  } catch (error) {
    console.error("Logging failed:", error);
      Swal.fire({
      title: "Oops",
      text: "Something went wrong",
      icon: "error"
    });
    
  }

  });

});

