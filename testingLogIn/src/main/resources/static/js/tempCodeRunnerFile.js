function toggleModal(modalId, show = true) {
        const modal = document.getElementById(modalId);
        if (modal) {
            if (show) {
                modal.style.visibility = "visible";
                modal.style.opacity = "1";
    
                // Change button text for edit modals
                if (modalId.includes("Edit")) {
                    const updateBtn = modal.querySelector(".btn-update");
                    const cancelBtn = modal.querySelector(".btn-cancel");
                    if (updateBtn) updateBtn.style.display = "inline-block";
                    if (cancelBtn) cancelBtn.style.display = "inline-block";
                }
            } else {
                modal.style.visibility = "hidden";
                modal.style.opacity = "0";
    
                // Reset buttons when closing edit modal
                if (modalId.includes("Edit")) {
                    const updateBtn = modal.querySelector(".btn-update");
                    const cancelBtn = modal.querySelector(".btn-cancel");
                    if (updateBtn) updateBtn.style.display = "none";
                    if (cancelBtn) cancelBtn.style.display = "none";
                }
            }
        } else {
            console.warn(`Modal with ID '${modalId}' not found`);
        }
    }
    