/**
 * Password Reset Logic
 * Handles password reset using token from URL query parameters.
 */
document.querySelector(".submit-btn").addEventListener("click", async () => {


  const apiUrl = "https://bestflix-budz.onrender.com";


    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");
    const newPassword = document.querySelector(".input-n-p").value;

    // Validate new password
    if (!newPassword || newPassword.length < 6) {
      Swal.fire({
      title: "Oops",
      text: "Password must be at least 6 characters long!",
      icon: "warning"
    });
      return;
    }

    // Send reset request
    try {
      const response = await fetch(`${apiUrl}/reset-password`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token: token,
          newPassword: newPassword
        })
      });


      if (response.ok) {
        Swal.fire({
        title: "Great!",
        text: "Password reset successful! Please log in again.",
        icon: "success"
      });
        window.location.href = "login.html";
      } else {
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
      text: "Something went wrong while resetting your password.",
      icon: "error"
    });
    }
  });