document.addEventListener("DOMContentLoaded", () => {
    const MAX_IMAGE_SIZE_MB = 5;
  
    const logoInput = document.getElementById("logoUpload");
    const previewLogo = document.getElementById("previewLogo");
    const coverInput = document.getElementById("coverUpload");
    const previewCover = document.getElementById("coverPreview");
  
    // ✅ Logo Preview
    logoInput.addEventListener("change", (event) => {
      const file = event.target.files[0];
      if (file) {
        if (!file.type.startsWith("image/")) {
          showErrorModal("❌ Only image files are allowed for the logo.");
          logoInput.value = "";
          return;
        }
        if (file.size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
          showErrorModal(`❌ Logo size exceeds ${MAX_IMAGE_SIZE_MB}MB.`);
          logoInput.value = "";
          return;
        }
  
        const reader = new FileReader();
        reader.onload = (e) => {
          previewLogo.src = e.target.result;
          previewLogo.style.display = "block";
        };
        reader.readAsDataURL(file);
      } else {
        previewLogo.src = "/images/default-logo.png";
        previewLogo.style.display = "block";
      }
    });
  
    // ✅ Cover Preview
    coverInput.addEventListener("change", (event) => {
      const file = event.target.files[0];
      if (file) {
        if (!file.type.startsWith("image/")) {
          showErrorModal("❌ Only image files are allowed for the cover photo.");
          coverInput.value = "";
          return;
        }
        if (file.size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
          showErrorModal(`❌ Cover photo size exceeds ${MAX_IMAGE_SIZE_MB}MB.`);
          coverInput.value = "";
          return;
        }
  
        const reader = new FileReader();
        reader.onload = (e) => {
          previewCover.src = e.target.result;
          previewCover.style.display = "block";
        };
        reader.readAsDataURL(file);
      } else {
        previewCover.src = "/images/default-cover.jpg";
        previewCover.style.display = "block";
      }
    });
  
    // ✅ Load saved theme color
  });
  
  // ✅ Submit using FormData (handles big files)
  async function updateSchoolSettings() {
    const form = new FormData();
  
    form.append("schoolName", document.getElementById("schoolName").value);
    form.append("schoolAddress", document.getElementById("schoolAddress").value);
    form.append("schoolEmail", document.getElementById("schoolEmail").value);
    form.append("schoolContact", document.getElementById("schoolContact").value);
    form.append("graduatingLevel", document.getElementById("graduatingLevel").value);
  
    const logoFile = document.getElementById("logoUpload").files[0];
    if (logoFile) {
      form.append("logo", logoFile); // This is the raw file
    }
  
    const coverFile = document.getElementById("coverUpload").files[0];
    if (coverFile) {
      form.append("cover", coverFile);
    }

  console.log("Sending now");
    fetch("/website-config/update", {
      method: "POST",
      body: form,
    })
      .then((response) => {
        if (!response.ok) {
          response.text().then((text) => {
            showErrorModal(`❌ Error: ${text}`);
          });
          return;
        }
        return response.text();
      })
      .then((text) => {
        showSuccessModal(`✅ Success: ${text}`);
      })
      .catch((error) => {
        console.error("🚨 Error:", error);
        showErrorModal(`❌ Error: Unable to proceed with the request.`);
      });
  }
  