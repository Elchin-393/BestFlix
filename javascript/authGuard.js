/**
 * Auth Guard
 * Redirects user to login if token is missing or expired.
 * Shows alert on expired session.
 */
(function () {
  /** @type {string|null} */
  const token = localStorage.getItem("jwtToken");

  if (!token) {
    window.location.href = "login.html";
    return;
  }

  try {
    /** @type {{exp: number}} */
    const payload = JSON.parse(atob(token.split(".")[1]));
    const exp = payload.exp * 1000;
    const now = Date.now();

    if (now > exp) {
      Swal.fire({
      title: "Oops",
      text: "Session Expired. Please log in again!",
      icon: "error"
    });
      localStorage.removeItem("jwtToken");
      window.location.href = "login.html";
    }
  } catch (err) {
    console.error("Invalid JWT format:", err);
    window.location.href = "login.html";
  }
})();