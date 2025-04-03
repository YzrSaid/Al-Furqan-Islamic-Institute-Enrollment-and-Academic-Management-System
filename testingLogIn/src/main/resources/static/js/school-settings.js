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
    form.addEventListener("submit",async (event) => {
        event.preventDefault();

        const formValues = {
            schoolName: document.getElementById('schoolName').value,
            schoolAddress: document.getElementById('schoolAddress').value,
            schoolEmail: document.getElementById('schoolEmail').value,
            schoolContact: document.getElementById('schoolContact').value,
            themeColor: document.getElementById('colorPicker').value,
            graduatingLevel : document.getElementById('graduatingLevel').value,
            logoBase64: null
        };

        const logoFile = document.getElementById('logoUpload').files[0];
        if (logoFile) {
            formValues.logoBase64 = await fileToJavaBase64(logoFile);
        }

        fetch("/website-config/update", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body : JSON.stringify(formValues)
            })
            .then(response => {
                if(!response.ok){
                    alert(response.text());
                    return;
                }
                return response.text();
            })
            .then(text => {
                alert(text);
                window.location.reload();
            })
            .catch(error => {
                    console.error("🚨 Error calling proceedToPayment API:", error);
                    alert("❌ Error: Unable to proceed with payment.");
            });
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
                    const base64Data = reader.result.split(',')[1];

                    // Validate Base64 result
                    if (!base64Data) {
                        throw new Error("Base64 conversion failed");
                    }

                    resolve(base64Data);
                } catch (error) {
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

    const savedColor = localStorage.getItem("themeColor");
    if (savedColor) {
        colorPicker.value = savedColor;
        document.documentElement.style.setProperty("--theme-color", savedColor);
    }
});
