document.addEventListener("DOMContentLoaded", () => {
    const logoInput = document.getElementById("logoUpload");
    const previewLogo = document.getElementById("previewLogo");
    const coverInput = document.getElementById("coverUpload"); // New input for cover photo
    const previewCover = document.getElementById("coverPreview"); // New preview image for cover photo
    const colorPicker = document.getElementById("colorPicker");
  
    // ✅ Preview uploaded logo
    logoInput.addEventListener("change", (event) => {
      const file = event.target.files[0];
  
      if (file) {
        if (file.type.startsWith("image/")) {
          // ✅ Only accept image files
          const reader = new FileReader();
  
          reader.onload = (e) => {
            previewLogo.src = e.target.result; // ✅ Display preview
            previewLogo.style.display = "block"; // Show the preview
          };
  
          reader.readAsDataURL(file);
        } else {
          // Show error modal for invalid file
          showErrorModal("❌ Please upload a valid image file for the logo.");
          logoInput.value = ""; // Clear invalid file
        }
      } else {
        // ✅ Display default logo if no file is uploaded
        previewLogo.src = "/images/default-logo.png";
        previewLogo.style.display = "block";
      }
    });
  
    // ✅ Preview uploaded cover photo
    coverInput.addEventListener("change", (event) => {
      const file = event.target.files[0];
  
      if (file) {
        if (file.type.startsWith("image/")) {
          // ✅ Only accept image files
          const reader = new FileReader();
  
          reader.onload = (e) => {
            previewCover.src = e.target.result; // ✅ Display preview
            previewCover.style.display = "block"; // Show the preview
          };
  
          reader.readAsDataURL(file);
        } else {
          // Show error modal for invalid file
          showErrorModal("❌ Please upload a valid image file for the cover photo.");
          coverInput.value = ""; // Clear invalid file
        }
      } else {
        // ✅ Display default cover if no file is uploaded
        previewCover.src = "/images/default-cover.jpg"; // Default cover image
        previewCover.style.display = "block";
      }
    });
  
    const savedColor = localStorage.getItem("themeColor");
    if (savedColor) {
      colorPicker.value = savedColor;
      document.documentElement.style.setProperty("--theme-color", savedColor);
    }
  });
  

function fileToJavaBase64(file) {
  return new Promise((resolve, reject) => {
    // 1. Validate input
    if (!file || !(file instanceof File)) {
      reject(new Error("Invalid file input"));
      return;
    }

    // 2. Create file reader
    const reader = new FileReader();

    // 3. Set up event handlers
    reader.onload = () => {
      try {
        // Extract Base64 part after comma (removes "data:*/*;base64," prefix)
        const base64Data = reader.result.split(",")[1];

        // Validate Base64 result
        if (!base64Data) {
          throw new Error("Base64 conversion failed");
        }

        resolve(base64Data);
      } catch (error) {
        // Show error modal if Base64 conversion fails
        showErrorModal(`❌ Error: ${error.message}`);
        reject(error);
      }
    };

    reader.onerror = (error) => reject(error);
    reader.onprogress = (event) => {
      // Optional: Add progress tracking for large files
      console.log(`Loaded ${event.loaded} of ${event.total} bytes`);
    };

    // 4. Start reading (returns Base64-encoded string)
    reader.readAsDataURL(file);
  });
}
async function updateSchoolSettings() {
  const formValues = {
    schoolName: document.getElementById("schoolName").value,
    schoolAddress: document.getElementById("schoolAddress").value,
    schoolEmail: document.getElementById("schoolEmail").value,
    schoolContact: document.getElementById("schoolContact").value,
    themeColor: document.getElementById("colorPicker").value,
    graduatingLevel: document.getElementById("graduatingLevel").value,
    logoBase64: null,
  };

  const logoFile = document.getElementById("logoUpload").files[0];
  if (logoFile) {
    formValues.logoBase64 = await fileToJavaBase64(logoFile);
  }

  fetch("/website-config/update", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(formValues),
  })
    .then((response) => {
      if (!response.ok) {
        // Show error modal if the response is not ok
        response.text().then((text) => {
          showErrorModal(`❌ Error: ${text}`);
        });
        return;
      }
      return response.text();
    })
    .then((text) => {
      // Show success modal when the update is successful
      showSuccessModal(`✅ Success: ${text}`);
    })
    .catch((error) => {
      console.error("🚨 Error:", error);
      // Show error modal for any unexpected errors
      showErrorModal(`❌ Error: Unable to proceed with the request.`);
    });
}
