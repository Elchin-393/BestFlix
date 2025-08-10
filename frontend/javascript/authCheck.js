/**
 * Auth Check Script
 * Determines whether the user is logged in based on JWT validity.
 * Updates sign-in button to redirect to appropriate page.
 */
document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("jwtToken");
  const signInBtn = document.querySelector("#header .sign-in");
  const signInText = signInBtn ? signInBtn.querySelector("h4") : null;

  if (!signInBtn || !signInText) return; 

  /** @type {boolean} */
  let isValidToken = false;

  if (token) {
    try {
      /** @type {{exp: number}} */
      const payload = JSON.parse(atob(token.split(".")[1]));
      const exp = payload.exp * 1000;
      const now = Date.now();
      isValidToken = now < exp;
    } catch (err) {
      console.error("Invalid JWT format:", err);
    }
  }

  if (isValidToken) {
    signInText.textContent = "Home";
    signInBtn.onclick = () => window.location.href = "mymovies.html"; 
    signInBtn.classList.add("logged-in"); 
  } else {
    signInText.textContent = "Sign in";
    signInBtn.onclick = () => window.location.href = "/html/login.html";
    signInBtn.classList.remove("logged-in");
  }
});

