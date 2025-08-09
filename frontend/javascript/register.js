/**
 * Registration Page Logic
 * Handles user input validation, registration form submission,
 * and redirects on successful signup.
 */

// 🔁 Redirects to login page when "Login" link is clicked
const login = document.querySelector("#register a")

login.addEventListener("click", function() {

  window.location.href = "login.html";
})

document.addEventListener("DOMContentLoaded", () => {

  const apiUrl = "https://bestflix-budz.onrender.com";


  const logbtn = document.querySelector(".log-btn");

  /**
   * Validates form fields and submits registration payload.
   * @param {MouseEvent} e
   */
  logbtn.addEventListener("click", async (e) => {
    e.preventDefault();

    const username = document.querySelector(".input-name").value.trim();
    const email    = document.querySelector(".input-email").value.trim();
    const password = document.querySelector(".input-password").value;
    const cpassword = document.querySelector(".input-c-password").value;

    // Input validation
    if (!username || !email || !password) {
      Swal.fire({
      title: "Oops",
      text: "All fields are required.",
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
      title: "Oops",
      text: "Password must be at least 6 characters long!",
      icon: "warning"
    });
    return;
  }

  if(password !== cpassword){
    Swal.fire({
      title: "Oops",
      text: "Password fields must be equal",
      icon: "warning"
    });
    return;
  }

  if (!email || !/^\S+@\S+\.\S+$/.test(email)) {
    Swal.fire({
      title: "Oops",
      text: "Please enter a valid email address!",
      icon: "warning"
    });
    return;
  }

    const payload = { username, email, password };

    // Submit registration
    try {
      const response = await fetch(`${apiUrl}/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errMsg = await response.text();
        throw new Error(errMsg || response.statusText);
      }

      const message = await response.text();
      await Swal.fire({
      title: "Good Job!",
      text: message,
      icon: "success"
    });
                 
      window.location.href = "login.html";  
    } catch (err) {
      console.error("Registration failed:", err);
      Swal.fire({
      title: "Oops",
      text: "Registration error: " + err.message,
      icon: "warning"
    });
    }
  });
});


