document.addEventListener("DOMContentLoaded", () => {
    const logoInput = document.getElementById("logoUpload");
    const previewLogo = document.getElementById("previewLogo");
    const colorPicker = document.getElementById("colorPicker");
    const form = document.getElementById("schoolSettingsForm");

    // ✅ Preview uploaded logo
    logoInput.addEventListener("change", (event) => {
        const file = event.target.files[0];

        if (file) {
            if (file.type.startsWith("image/")) {  // ✅ Only accept image files
                const reader = new FileReader();

                reader.onload = (e) => {
                    previewLogo.src = e.target.result;  // ✅ Display preview
                    previewLogo.style.display = "block";  // Show the preview
                };

                reader.readAsDataURL(file);
            } else {
                alert("Please upload a valid image file.");
                logoInput.value = "";  // Clear invalid file
            }
        } else {
            // ✅ Display default logo if no file is uploaded
            previewLogo.src = "/images/default-logo.png";  
            previewLogo.style.display = "block";
        }
    });

    // ✅ Save settings with form submission
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const formData = new FormData();
        formData.append("logo", logoInput.files[0]);
        formData.append("themeColor", colorPicker.value);

        fetch("/settings/update-school", {
            method: "POST",
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("Settings saved successfully!");
                document.documentElement.style.setProperty("--theme-color", colorPicker.value);
            } else {
                alert("Failed to save settings!");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while saving.");
        });
    });

    // ✅ Apply saved theme color on page load
    const savedColor = localStorage.getItem("themeColor");
    if (savedColor) {
        colorPicker.value = savedColor;
        document.documentElement.style.setProperty("--theme-color", savedColor);
    }
});
