
/**
 * Forgot Password Script
 * Validates email input and sends reset request to backend.
 */
document.querySelector(".send-btn").addEventListener("click", async () => {

  const apiUrl = "http://localhost:8080";


  /** @type {string} */
    const email = document.querySelector(".input-e").value.trim();

    if (!email) {
      Swal.fire({
      title: "Oops",
      text: "Please enter your email",
      icon: "warning"
    });
      return;
    }

    
    /** @type {RegExp} */
  const emailRegex = /^\S+@\S+\.\S+$/;

  if (!emailRegex.test(email)) {
    Swal.fire({
      title: "Oops",
      text: "Please enter a valid email address!",
      icon: "warning"
    });
    return;
  }

    try {
      /** @type {Response} */
      const response = await fetch(`${apiUrl}/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email })
      });

      if (response.ok) {
        Swal.fire({
        title: "Good Job!",
        text: "If that email exists, you’ll receive a reset link shortly.",
        icon: "success"
      });
      } else if(!response.ok) {
        const errorData = await response.json();
        const message = errorData?.errorDetails?.message || "Unknown error";
        Swal.fire({
        title: "Oops",
        text: message,
        icon: "error"
      });
      }
    } catch (err) {
      console.error(err);
      Swal.fire({
        title: "Oops",
        text: "Something went wrong. Please try again.",
        icon: "error"
      });
    }
  });