// this is for the log in label effect
document.addEventListener("DOMContentLoaded", function () {
  function updateLabels() {
    document.querySelectorAll(".input-group-input").forEach((input) => {
      if (input.value.trim() !== "") {
        input.classList.add("filled");
      } else {
        input.classList.remove("filled");
      }
    });
  }

  // Initial check
  updateLabels();

  // Check on user input
  document.querySelectorAll(".input-group-input").forEach((input) => {
    input.addEventListener("input", updateLabels);
    input.addEventListener("change", updateLabels);
  });

  // Autofill detection using focus
  document.querySelectorAll(".input-group-input").forEach((input) => {
    input.addEventListener("focus", updateLabels);
    input.addEventListener("blur", updateLabels);
  });

  // Ensure autofill is detected
  setTimeout(updateLabels, 500);
  setTimeout(updateLabels, 1000);
});

// function toggleSidebar() {
//   let sidebar = document.getElementById("sidebar");
//   let content = document.getElementById("content");
//   let stickyHeader = document.querySelector(".sticky-header");

//   // this is for the report-main-btns (reports)
//   let reportBtnsDiv = document.querySelector(".report-main-btns");

//   let table = document.querySelector(".table-wrapper");

//   let searchDiv = document.querySelector(".search-div");
//   let topBar = document.querySelector(".topbar");
//   let textLink = document.querySelector(".text-link");

//   if (sidebar.classList.contains("collapsed-sidebar")) {
//     // open header
//     sidebar.classList.remove("collapsed-sidebar");
//     content.classList.remove("collapsed-content");
//     content.style.marginLeft = "320px";

//     // report-main-btns
//     reportBtnsDiv.style.padding = "0 15px";

//     // stickyHeader.style.padding = "10px 20px";
//     stickyHeader.style.padding = "10px 1rem";
//     searchDiv.style.width = "100%";
//     searchDiv.style.padding = "0";
//     searchDiv.style.margin = "0 0 20px 0";
//     table.style.padding = "0";

//     // schedBoard.style.padding = "200px";

//     textLink.style.width = "100%";
//     textLink.style.padding = "15px 0";

//     topBar.style.padding = "10px 0";
//   } else {
//     // close header
//     sidebar.classList.add("collapsed-sidebar");
//     content.classList.add("collapsed-content");
//     content.style.marginLeft = "0";

//     // report-main-btns
//     reportBtnsDiv.style.padding = "0 8.8rem";

//     stickyHeader.style.padding = "10px 3.125rem";
//     searchDiv.style.width = "100%";
//     searchDiv.style.padding = "0 0px";
//     searchDiv.style.margin = "0 10px 20px 10px";
//     table.style.padding = "0 35px";
//     table.style.width = "80%";

//     textLink.style.width = "100%";
//     textLink.style.padding = "15px 0";

//     topBar.style.padding = "10px 0.1rem";
//   }
// }

function toggleSidebar() {
  let sidebar = document.getElementById("sidebar");
  let content = document.getElementById("content");
  let stickyHeader = document.querySelector(".sticky-header");
  let reportBtnsDiv = document.querySelector(".report-main-btns");
  let tableWrapper = document.querySelector(".table-wrapper"); // Corrected this
  let searchDiv = document.querySelector(".search-div");
  let topBar = document.querySelector(".topbar");
  let textLink = document.querySelector(".text-link");

  const isCollapsed = sidebar.classList.contains("collapsed-sidebar");

  if (isCollapsed) {
    // OPEN sidebar
    sidebar.classList.remove("collapsed-sidebar");
    content.classList.remove("collapsed-content");
    content.style.marginLeft = "320px";

    reportBtnsDiv.style.padding = "0 15px";
    stickyHeader.style.padding = "10px 1rem";
    searchDiv.style.width = "100%";
    searchDiv.style.padding = "0";
    searchDiv.style.margin = "0 0 20px 0";

    textLink.style.width = "100%";
    textLink.style.padding = "15px 0";

    topBar.style.padding = "10px 0";

    // Apply sidebar-open class when sidebar is OPEN
    tableWrapper.classList.remove("sidebar-closed");
    tableWrapper.classList.add("sidebar-open");
  } else {
    // CLOSE sidebar
    sidebar.classList.add("collapsed-sidebar");
    content.classList.add("collapsed-content");
    content.style.marginLeft = "0";

    reportBtnsDiv.style.padding = "0 8.8rem";
    stickyHeader.style.padding = "10px 3.125rem";
    searchDiv.style.width = "100%";
    searchDiv.style.padding = "0";
    searchDiv.style.margin = "0 10px 20px 10px";

    textLink.style.width = "100%";
    textLink.style.padding = "15px 0";

    topBar.style.padding = "10px 0.1rem";

    // Apply sidebar-closed class when sidebar is CLOSED
    tableWrapper.classList.remove("sidebar-open");
    tableWrapper.classList.add("sidebar-closed");
  }
}

// Function to toggle the dropdown menu
function toggleDropdown(id) {
  const dropdownMenu = document.getElementById(id);

  if (!dropdownMenu) return;

  // Close all other dropdowns before toggling the clicked one
  document.querySelectorAll(".dropdown-content").forEach((dropdown) => {
    if (dropdown.id !== id) {
      dropdown.classList.remove("show");
    }
  });

  // Toggle the selected dropdown
  dropdownMenu.classList.toggle("show");
}

// Close the dropdown menu if the user clicks outside of it
window.onclick = function (event) {
  if (
    !event.target.closest(".dropdown-toggle") &&
    !event.target.closest(".dropdown-content")
  ) {
    document.querySelectorAll(".dropdown-content").forEach((dropdown) => {
      dropdown.classList.remove("show");
    });
  }
};
function toggleSubMenu(submenuId, event) {
  event.preventDefault();

  let submenu = document.getElementById(submenuId);
  let arrowIconImg =
    submenu.previousElementSibling.querySelector(".arrow-icon img");
  let isOpen = submenu.classList.contains("open");

  // Close all unrelated submenus EXCEPT the clicked one and the ones with active items
  document.querySelectorAll(".submenu").forEach((otherSubmenu) => {
    let hasActiveItem = otherSubmenu.querySelector(
      ".submenu-item.active, .submenu-item.second-active"
    );

    if (otherSubmenu !== submenu && !hasActiveItem) {
      otherSubmenu.classList.remove("open");
      otherSubmenu.style.display = "none";

      let otherArrowIcon =
        otherSubmenu.previousElementSibling?.querySelector(".arrow-icon img");
      if (otherArrowIcon) {
        otherArrowIcon.src = "/images/icons/arrow-down.png";
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);
  submenu.style.display = isOpen ? "none" : "block";
  arrowIconImg.src = isOpen
    ? "/images/icons/arrow-down.png"
    : "/images/icons/greater-than.png";

  // Save submenu state in localStorage
  let savedSubmenus = JSON.parse(localStorage.getItem("openedSubmenus")) || [];

  if (!isOpen) {
    if (!savedSubmenus.includes(submenuId)) {
      savedSubmenus.push(submenuId);
    }
  } else {
    savedSubmenus = savedSubmenus.filter((id) => id !== submenuId);
  }

  localStorage.setItem("openedSubmenus", JSON.stringify(savedSubmenus));
}

// Ensure only active submenus stay open on reload
document.addEventListener("DOMContentLoaded", function () {
  let savedSubmenus = JSON.parse(localStorage.getItem("openedSubmenus")) || [];
  let updatedSubmenus = [];

  document.querySelectorAll(".submenu").forEach((submenu) => {
    // 🔥 Use a timeout to ensure dynamically added classes are detected
    setTimeout(() => {
      let hasActiveItem = submenu.querySelector(
        ".submenu-item.active, .submenu-item.second-active"
      );

      if (hasActiveItem) {
        // Keep active submenus open
        submenu.classList.add("open");

        // Ensure parent menus are opened
        let parentMenu = submenu.closest(".submenu");
        if (parentMenu) {
          parentMenu.classList.add("open");
        }

        // Update arrow icon
        let arrowIconImg =
          submenu.previousElementSibling?.querySelector(".arrow-icon img");
        if (arrowIconImg) {
          arrowIconImg.src = "/images/icons/greater-than.png";
        }

        // Add active submenu to localStorage state
        updatedSubmenus.push(submenu.id);
      } else if (savedSubmenus.includes(submenu.id)) {
        // Close inactive saved submenus
        submenu.classList.remove("open");

        let arrowIconImg =
          submenu.previousElementSibling?.querySelector(".arrow-icon img");
        if (arrowIconImg) {
          arrowIconImg.src = "/images/icons/arrow-down.png";
        }
      }

      // Update localStorage with only active submenus
      localStorage.setItem("openedSubmenus", JSON.stringify(updatedSubmenus));
    }, 0); // Timeout ensures dynamic classes are detected
  });
});

// ✅ Clear all submenus ONLY when clicking on non-submenu links
function clearSubmenus() {
  document.querySelectorAll(".submenu").forEach((submenu) => {
    submenu.classList.remove("open");
  });

  document.querySelectorAll(".arrow-icon img").forEach((img) => {
    img.src = "/images/icons/arrow-down.png";
  });

  localStorage.removeItem("openedSubmenus"); // Clear saved submenu states
}

document.addEventListener("DOMContentLoaded", function () {
  // Select all dropdown links inside dropdown-status-content
  const dropdownLinks = document.querySelectorAll(".dropdown-status-content a");

  // this is for listing/registration status buttons
  const dropdownLinksListing = document.querySelectorAll("#dropdown-listing a");

  // this is for school year maintenance dropdown
  const dropdownLinksSchoolYear = document.querySelectorAll(
    "#dropdown-school-year a"
  );

  // this is for verifying an account
  const verificationBtns = document.querySelectorAll(
    ".dropdown-status-content-verify div"
  );

  // this is for manage accounts dropdown
  const dropdownLinksManageAccounts = document.querySelectorAll(
    "#dropdown-manage-accounts a"
  );

  const modal = document.getElementById("confirmationModal");

  const modalText = document.getElementById("modalText");
  const confirmButton = document.querySelector(".btn-confirm");
  const cancelButton = document.querySelector(".btn-cancel");

  //this is for listing/registration status buttons
  dropdownLinksListing.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "proceed-enrollment") {
        modalText.textContent = "Are you sure you want to proceed?";
      } else if (action === "cancel-enrollment") {
        modalText.textContent = "Are you sure you want to cancel registration?";
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "proceed-enrollment") {
          console.log(action + " confirmed!");
          // code for verification
        } else if (action === "cancel-enrollment") {
          console.log("boogsh");
          // code for reject
        }
        modal.style.display = "none";
      };

      // Handle the cancel button
      cancelButton.onclick = function () {
        modal.style.display = "none";
      };
    });
  });

  // Managing accounts dropdown
  dropdownLinksManageAccounts.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action from the clicked link

      if (action === "Restrict") {
        modalText.textContent =
          "Are you sure you want to restrict this account?";
      } else if (action === "Delete Account") {
        modalText.textContent = "Are you sure you want to delete this account?";
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "Restrict") {
          console.log(action + " confirmed!");
          // Code for restriction
        } else if (action === "Delete Account") {
          console.log("boogsh");
          // Code for deletion
        }
        modal.style.display = "none";
      };

      // Handle the cancel button
      cancelButton.onclick = function () {
        modal.style.display = "none";
      };
    });
  });

  // this is for school year under maintenance
  dropdownLinksSchoolYear.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action

      if (action === "make-active") {
        modalText.textContent =
          "Are you sure you want to make this school year active?";
      } else if (action === "make-inactive") {
        modalText.textContent =
          "Are you sure you want to make this school year inactive?";
      }
      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "make-active") {
          // code for making the school year active
        } else if (action === "make-inactive") {
          // code for making the school year inactive
        } else if (action === "archive") {
          // code for archiving the school year
        }
        modal.style.display = "none"; // Close modal after action
      };

      // Handle the cancel button
      cancelButton.onclick = function () {
        modal.style.display = "none";
      };
    });
  });

  // this is for verification of user accounts
  verificationBtns.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "verify") {
        modalText.textContent = "Are you sure you want to verify this account?";
      } else if (action === "reject") {
        modalText.textContent = "Are you sure you want to reject this account?";
      } else {
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "verify") {
          console.log(action + " confirmed!");
          // code for verification
        } else if (action === "reject") {
          console.log("boogsh");
          // code for reject
        }
        modal.style.display = "none";
      };

      // Handle the cancel button
      cancelButton.onclick = function () {
        modal.style.display = "none";
      };
    });
  });
});

// this is for redirect for the maintenance schedule (teacher-maintenance)
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("a.view-sched").forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault();
      window.location.href = "/schedule/sched-board";
    });
  });
});

// this is for redirect in grade management (main) to grade-per-class
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("a.view-per-class").forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent default navigation
      window.location.href = "/grade-management/grade-per-class"; // Navigate to the sched board page
    });
  });
});

// this is for redirect in grade-per-class to grade-per-subject
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("a.view-per-subject").forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent default navigation
      window.location.href = "/grade-per-class";
    });
  });
});

// Function to enable inputs and checkboxes
window.enableFormInputs = function () {
  // Enable all inputs, selects, checkboxes, and radio buttons
  document.querySelectorAll("input, select").forEach((input) => {
    if (input.tagName === "SELECT") {
      input.disabled = false; // Enable selects
    } else if (input.type === "checkbox" || input.type === "radio") {
      input.disabled = false; // Enable checkboxes and radio buttons
    } else {
      input.readOnly = false; // Set text-based inputs as editable
      input.disabled = false; // Enable other inputs like text, number, password, etc.
    }
  });
};
// Function to disable inputs and checkboxes after they are populated
window.disableFormInput = function () {
  // Disable all inputs, selects, checkboxes, and radio buttons
  document.querySelectorAll("input, select").forEach((input) => {
    if (input.tagName === "SELECT") {
      input.disabled = true; // Disable selects
    } else if (input.type === "checkbox" || input.type === "radio") {
      input.disabled = true; // Disable checkboxes and radio buttons
    } else {
      input.readOnly = true; // Set other text-based inputs as readonly
      input.disabled = true; // Disable other inputs like text, number, password, etc.
    }
  });
};

document.addEventListener("DOMContentLoaded", function () {
  function toggleModal(modalId, show = true, message = "") {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    if (show) {
      modal.classList.add("show");
      modal.style.visibility = "visible";
      modal.style.opacity = "1";
      modal.style.pointerEvents = "auto"; // Enable interaction

      // Disable page scroll
      document.body.style.overflow = "hidden";
    } else {
      modal.classList.remove("show");
      modal.style.visibility = "hidden";
      modal.style.opacity = "0";
      modal.style.pointerEvents = "none"; // Ensure it doesn't block clicks

      // Enable page scroll
      document.body.style.overflow = "auto";
    }

    // If it's a confirmation modal, update the text
    if (modalId === "confirmationModal" && message) {
      document.getElementById("modalText").textContent = message;

      // Reset confirm button properly to avoid event listener issues
      let confirmBtn = document.getElementById("confirmAction");
      let newConfirmBtn = confirmBtn.cloneNode(true);
      confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
    }

    if (modalId.includes("Edit") && modalId !== "studentInformationEditModal") {
      console.log("yawa");
      const confirmBtn = modal.querySelector(".btn-confirm");
      const cancelBtn = modal.querySelector(".btn-cancel");
      const inputs = modal.querySelectorAll("input, textarea, select");

      // Only disable inputs inside the current modal
      inputs.forEach((input) => {
        input.disabled = true;
      });

      // Set initial button states
      confirmBtn.textContent = "Edit";
      cancelBtn.textContent = "Close";
      confirmBtn.setAttribute("data-mode", "edit");
    }
  }

  document.body.addEventListener("click", function (event) {
    const editButton = event.target.closest("#editButton");
    const confirmActionButton = event.target.closest("#confirmAction");

    const inputs = document.querySelectorAll(
      "#schoolSettingsForm input, #schoolSettingsForm select, #schoolSettingsForm textarea"
    );

    if (editButton) {
      let currentMode = editButton.getAttribute("data-mode") || "edit";

      if (currentMode === "edit") {
        inputs.forEach((input) => {
          if (input.tagName === "SELECT") {
            input.disabled = false;
          } else {
            input.readOnly = false;
            input.disabled = false;
          }
        });

        editButton.textContent = "Update";
        editButton.setAttribute("data-mode", "update");
      } else if (currentMode === "update") {
        const message = "Are you sure you want to update the school settings?";
        const modalText = document.getElementById("modalText");
        const action = editButton.getAttribute("data-action") || "";

        if (modalText) modalText.textContent = message;

        const confirmAction = document.getElementById("confirmAction");
        if (confirmAction) {
          confirmAction.setAttribute("data-confirm-action", action);
        }

        if (typeof toggleModal === "function") {
          toggleModal("confirmationModal", true);
        }
      }
    }

    if (confirmActionButton) {
      const action = confirmActionButton.getAttribute("data-confirm-action");

      if (action === "updateSchoolSettings") {
        // ✅ Validate first
        if (!validateForm("schoolSettingsForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        }

        // ✅ If passed, update settings
        updateSchoolSettings();

        // ✅ Now do UI stuff
        const editBtn = document.getElementById("editButton");
        if (editBtn) {
          editBtn.textContent = "Edit";
          editBtn.setAttribute("data-mode", "edit");
        }

        inputs.forEach((input) => {
          input.disabled = true;
          input.readOnly = true;
        });

        // ✅ Hide modal
        if (typeof toggleModal === "function") {
          toggleModal("confirmationModal", false);
        }
      }
    }
  });

  async function handleConfirmAction(action, event) {
    console.log("Action triggered:", action);
    if (event && typeof event.preventDefault === "function") {
      event.preventDefault();
      console.log("Prevented default behavior");
    } else {
      console.warn("⚠️ Warning: Event is missing or invalid");
    }

    if (!action) {
      console.error("❌ Error: Action is undefined!");
      return;
    }

    let confirmationModal = document.getElementById("confirmationModal");
    confirmationModal.style.visibility = "hidden";
    confirmationModal.style.opacity = "0";
    console.log("Closed confirmation modal");

    switch (action) {
      case "updateSchoolSettings":
        // This is for updating/editing the school settings
        //   updateSchoolSettings();
        closeConfirmationModal();
        break;
      case "editTransfereeReq":
        updateTransfRequirement();
        break;
      case "saveStudentOrTransferee":
        saveStudentOrTransferee();
        break;
      case "saveSchedule":
        saveSchedule();
        break;
      case "addNewStudent":
        closeConfirmationModal();
        break;
      case "addTransfereeReq":
        // This is for adding transferee requirement (settings page)
        if (!validateForm("transfereeReqForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          addTransfereeReq();
          closeConfirmationModal();
        }
        break;
      case "deleteTransfereeReq":
        // This is to delete transferee requirement (settings page)
        deleteTransfereeReq(selectedTransfereeReqId);
        break;
      case "addGradeLevel":
        // This case is for adding new grade level
        if (!validateForm("gradeLevelForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          addGradeLevel();
          closeConfirmationModal();
        }
        break;
      case "editGradeLevel":
        // This case is for editing new grade level
        if (!validateForm("gradeLevelEditForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          // This is to check if the password field is not empty before verifying it
          if (!validateForm("confirmationModal")) {
            showErrorModal("⚠️ Please enter the Admin Password!");
            return;
          } else {
            // If its not empty, it will now verify the password
            if (await validateAdminPassword()) {
              editGradeLevel();
              closeConfirmationModal();
            } else {
              showErrorModal("⚠️ Incorrect Admin Password!");
              return;
            }
          }
        }
        break;
      case "deleteGradeLevel":
        deleteGradeLevel(selectedGradeLevelId);
        break;
      case "makeSchoolYearInactive":
        actionUrl = "/school-year/inactivate";
        method = "PUT";
        formData.append(
          "schoolYearId",
          document.getElementById("schoolYearId").value
        );
        break;
      case "makeSchoolYearArchive":
        actionUrl = "/school-year/archive";
        method = "PUT";
        formData.append(
          "schoolYearId",
          document.getElementById("schoolYearId").value
        );
        break;
      case "clearSched":
        window.clearSched();
        alert("Schedule cleared successfully!");
        break;
      case "makeSchoolYearActive":
        actionUrl = "/school-year/activate";
        method = "PUT";
        formData.append(
          "schoolYearId",
          document.getElementById("schoolYearId").value
        );
        break;
      //      case "addStudent":
      //        actionUrl = "/student/add";
      //        method = "POST";
      //        formData.append(
      //          "studentName",
      //          document.getElementById("studentName").value
      //        );
      //        console.log(formData);
      //        alert('stopper');
      //        break;
      case "addSection":
        if (!validateForm("sectionForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          addSection();
          closeConfirmationModal();
        }
        break;
      case "editSection":
        // This case is for adding subject level
        if (!validateForm("sectionEditForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          editSection();
          closeConfirmationModal();
        }
        break;
      case "deleteSection":
        deleteSectionNow();
        break;
      case "addSubject":
        // This case is for adding subject level
        if (!validateForm("subjectForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          addSubject();
          closeConfirmationModal();
        }

        break;
      case "editSubject":
        // This case is for adding subject level
        if (!validateForm("subjectEditForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          editSubject();
          closeConfirmationModal();
        }
        break;
      case "deleteSubject":
        deleteSubject(selectedSubjectId);
        break;
      case "addTeacher":
        addTeacher();
        break;
      case "editTeacher":
        alert("Edit Teacher");
        break;
      case "verifyAccount":
        verifyAccount(selectedVerificationId, selectedRole);
        break;
      case "rejectAccount":
        rejectAccount(selectedVerificationId);
        break;
      case "restrictAccount":
        restrictUser(selectedManageAccountId);
        break;
      case "unrestrictAccount":
        unrestrictUser(selectedManageAccountId);
        break;
      case "editMyAccount":
        editMyAccount(selectedManageAccountId);
        break;
      case "addSchoolYear":
        // This case is for adding new grade level
        if (!validateForm("schoolYearForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          if (validateSchoolYear()) {
            addSchoolYear();
            closeConfirmationModal();
          }
        }
        break;
      case "changePassword":
        alert("Password for this account has been changed successfully!");
        break;
      case "saveSched":
        // code
        alert("Schedule Saved Successfully!");
        break;
      case "activateSemester":
        // if (!canActivateSemester(semNumber)) {
        //   console.log("Activation blocked.");
        //   return; // 🚀 Stop the function from running
        // } else {
        //     // activateSemester();
        // }
        let isAllowed = await canActivateSemester(semNumber); // ⏳ Wait for validation result

        if (!isAllowed) {
          console.log("Activation blocked.");
          return; // 🚀 Stop the function from running
        }
        activateSemester();
        break;
      case "deactivateSemester":
        deactivateSemester();
        break;
      case "addListingExisting":
        addListingOldStudent();
        break;
      case "addShcholarshipType":
        addNewScholarship();
        break;
      case "deleteScholarshipType":
        deletescholarship();
        break;
      case "transfereeAddListing":
        // This case is for adding transferee student to the listing/registration
        if (!validateForm("studentForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          transfereeAddListing();
          closeConfirmationModal();
        }
        break;
      //      case "studAddListing":
      //        // This case is for adding new student to the listing/registration
      //        if (!validateForm("studentForm")) { to delete soon
      //          showErrorModal("⚠️ Please fill in all required fields!");
      //          return;
      //        } else {
      //          studAddListing();
      //          closeConfirmationModal();
      //        }
      //        break;
      case "finishSemester":
        finishSemester();
        break;
      case "proceedAssessment":
        console.log(enrollmentIdLet);
        proceedToAssessment(enrollmentIdLet);
        toggleModal("gradeLevelModal", false);
        break;
      case "proceedToPayment":
        proceedToPayment(enrollmentIdLet, sectionNumberLet);
        break;
      case "proceedToEnrolled":
        proceedToEnrolled(enrollmentIdLet);
        break;
      case "addFee":
        // This case is for adding new fee
        if (!validateForm("feesManagementForm")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          addFee();
          closeConfirmationModal();
        }
        break;
      case "editFee":
        // This case is for editing the fee payment
        if (!validateForm("feesManagementEditModal")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          editFee();
          closeConfirmationModal();
        }
        break;
      case "editDistributable":
        if (!validateForm("distributableManagementEditModal")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          editDistributable();
          closeConfirmationModal();
        }
        break;
      //      case "deleteFee":
      //        deleteFee(selectedPaymentName);
      //        break;
      case "savePaymentTrans":
        savePaymentTrans();
        break;
      case "savePayment":
        // This case is for adding payment
        if (!validateForm("addPaymentModal")) {
          showErrorModal("⚠️ Please fill in all required fields!");
          return;
        } else {
          savePayment();
          closeConfirmationModal();
        }
        break;
      case "itemDistributed":
        confirmDistribution();
        break;
      case "addStudDisc":
        break;
      case "deleteStudDisc":
        break;
      case "updateRequirements":
        updateRequirements();
        break;
      case "editDiscounts":
        updateDiscounts();
        break;
      default:
        alert("Unknown action: " + action);
        return;
    }
  }

  async function validateAdminPassword() {
    // Ibutang diri ang code nimo mags, make sure butangi await sa pag fetch para astig
  }

  function closeConfirmationModal() {
    const modal = document.getElementById("confirmationModal");
    modal.classList.remove("show");
    modal.style.visibility = "hidden";
    modal.style.opacity = "0";
  }

  document.addEventListener("click", function (event) {
    // Get all open modals
    const openModals = document.querySelectorAll(".modal.show"); // Assuming 'show' is the class for visible modals

    openModals.forEach((modal) => {
      // Check if the click was outside of the modal
      if (!modal.contains(event.target) && event.target !== modal) {
        console.log(`⛔ Clicked outside ${modal.id}, preventing close...`);
        event.stopPropagation();
      }
    });
  });

  function validateForm(formId) {
    let form = document.getElementById(formId);
    if (!form) {
      console.error("❌ Form not found:", formId);
      return false;
    }

    let inputs = form.querySelectorAll("input[required], select[required]");
    let valid = true;

    inputs.forEach((input) => {
      if (!input.value.trim()) {
        valid = false;
        input.classList.add("error");
      } else {
        input.classList.remove("error");
      }
    });

    return valid;
  }

  window.showErrorModal = function (message) {
    const errorModal = document.getElementById("errorModal");
    const errorMessage = document.getElementById("errorMessage");

    if (errorModal && errorMessage) {
      errorMessage.textContent = message;
      errorModal.classList.remove("show");
      void errorModal.offsetWidth;
      errorModal.classList.add("show");

      // Ensure visibility and opacity are reset
      errorModal.style.visibility = "visible";
      errorModal.style.opacity = "1";
    } else {
      alert(`⚠️ ${message}`);
    }
  };
  window.showSuccessModal = function (message, reload = true) {
    const modal = document.getElementById("successModal");
    const modalMessage = document.getElementById("successModalMessage");
    const overlay = document.getElementById("successModalOverlay");

    if (modal && modalMessage && overlay) {
      modalMessage.innerHTML = message;

      modal.style.display = "flex";
      modal.style.flexDirection = "column";
      modal.style.justifyContent = "center";
      modal.style.alignItems = "center";

      overlay.style.display = "flex";

      setTimeout(() => {
        modal.style.display = "none";
        overlay.style.display = "none";

        if (
          message ===
          "✅ Account has been created successfully! This record will be sent for approval."
        ) {
          window.location.href = "/login";
        } else if (reload) {
          location.reload();
        }
      }, 1500); // 1.5 seconds
    } else {
      showErrorModal(
        `⚠️ Oops! Something went wrong. Please refresh or try again.`
      );
    }
  };

  document.querySelectorAll("[data-close-modal]").forEach((btn) => {
    btn.addEventListener("click", function () {
      let modalId = this.getAttribute("data-close-modal");
      let modal = document.getElementById(modalId);

      if (modal && modalId != "errorModal") {
        closeConfirmationModal();
        resetValidationErrors();
        clearForm();
      } else {
        closeConfirmationModal();
      }
    });
  });

  document
    .querySelectorAll("[data-close-modal='errorModal']")
    .forEach((btn) => {
      btn.addEventListener("click", function () {
        let errorModal = document.getElementById("errorModal");
        let studentForm = document.getElementById("studentForm");

        errorModal.style.visibility = "hidden";
        errorModal.style.opacity = "0";

        let confirmationModal = document.getElementById("confirmationModal");
        confirmationModal.style.visibility = "hidden";
        confirmationModal.style.opacity = "0";

        studentForm.style.visibility = "visible";
        studentForm.style.opacity = "1";
      });
    });

  document.body.addEventListener("submit", function (event) {
    if (event.target && event.target.id === "studentForm") {
      event.preventDefault();
      console.log("⛔ Form submission prevented!");
    }
  });

  document.body.addEventListener("click", function (event) {
    const target = event.target;
    let saveBtn = document.getElementById("saveBtn");

    // Open modal only if saveBtn's text is "Save"
    if (target.matches("[data-open-modal]")) {
      const modalId = target.getAttribute("data-open-modal");
      const message = target.getAttribute("data-message") || "";
      const action = target.getAttribute("data-action") || "";
      const requirePassword =
        target.getAttribute("data-require-password") === "true";

      document
        .getElementById("confirmAction")
        .setAttribute("data-confirm-action", action);

      // Show or hide admin password section
      const adminPassSection = document.getElementById("admin-pass-form");
      const securityText = document.getElementById("security-message");

      // Only modify the password section visibility if requirePassword is true
      if (requirePassword) {
        // Show the admin password section and security message
        if (adminPassSection) {
          adminPassSection.classList.remove("hidden");
          adminPassSection.style.display = "flex"; // Ensure it's visible when required
        }
        if (securityText) {
          securityText.classList.remove("hidden");
          securityText.style.display = "block"; // Ensure it's visible when required
        }
      } else {
        // Hide the admin password section and security message if no password is needed
        if (adminPassSection) {
          adminPassSection.classList.add("hidden");
          adminPassSection.style.display = "none"; // Hide the section when not required
        }
        if (securityText) {
          securityText.classList.add("hidden");
          securityText.style.display = "none"; // Hide the security message
        }
      }

      // Special logic for save button
      if (target === saveBtn) {
        if (saveBtn.textContent.trim() === "Add") {
          console.log("🔄 Changing text to Save...");
          saveBtn.textContent = "Save";
          return;
        }
      }

      toggleModal(modalId, true, message);
    }

    // Open modal only if saveBtn is already "Save"
    //   if (target.matches("[data-open-modal]")) {
    //     // if (saveBtn.textContent.trim() === "Save") {
    //     //   console.log("✅ Showing Modal");
    //     //   const modalId = target.getAttribute("data-open-modal");
    //     //   const message = target.getAttribute("data-message") || "";
    //     //   toggleModal(modalId, true, message);
    //     // } else {
    //     //     toggleModal(modalId, true, message);
    //     // //   console.log(
    //     // //     "❌ Modal won't open because text is:",
    //     // //     saveBtn.textContent.trim()
    //     // //   );
    //     // }
    //     const modalId = target.getAttribute("data-open-modal");
    //     const message = target.getAttribute("data-message") || "";

    //   }

    // Close modal
    if (target.matches("[data-close-modal]")) {
      const modalId = target.getAttribute("data-close-modal");
      toggleModal(modalId, false);
    }

    // Handle Edit/Update button inside Edit Modals
    if (target.matches(".btn-confirm")) {
      const modal = target.closest(".modal");
      const inputs = modal.querySelectorAll("input, textarea, select");
      const cancelBtn = modal.querySelector(".btn-cancel");

      if (target.getAttribute("data-mode") === "edit") {
        inputs.forEach((input) => {
          if (input.tagName === "SELECT") {
            input.disabled = false;
          } else {
            input.readOnly = false;
            input.disabled = false;
          }
        });

        target.textContent = "Update";
        cancelBtn.textContent = "Cancel";
        target.setAttribute("data-mode", "update");
        enableFormInputs();
      } else if (target.getAttribute("data-mode") === "update") {
        const requirePassword =
          target.getAttribute("data-require-password") === "true";

        // Show or hide admin password section
        const adminPassSection = document.getElementById("admin-pass-form");
        const securityText = document.getElementById("security-message");

        // Only modify the password section visibility if requirePassword is true
        if (requirePassword) {
          // Show the admin password section and security message
          if (adminPassSection) {
            adminPassSection.classList.remove("hidden");
            adminPassSection.style.display = "flex"; // Ensure it's visible when required
          }
          if (securityText) {
            securityText.classList.remove("hidden");
            securityText.style.display = "block"; // Ensure it's visible when required
          }
        } else {
          // Hide the admin password section and security message if no password is needed
          if (adminPassSection) {
            adminPassSection.classList.add("hidden");
            adminPassSection.style.display = "none"; // Hide the section when not required
          }
          if (securityText) {
            securityText.classList.add("hidden");
            securityText.style.display = "none"; // Hide the security message
          }
        }
        const message =
          target.getAttribute("data-message") ||
          "Are you sure you want to update this record?";
        const action = target.getAttribute("data-action") || ""; // Get action

        document.getElementById("modalText").textContent = message;
        document
          .getElementById("confirmAction")
          .setAttribute("data-confirm-action", action); // Store action
        toggleModal("confirmationModal", true);
      }
    }

    // Handle Confirm Action in Confirmation Modal
    if (target.id === "confirmAction") {
      const action = target.getAttribute("data-confirm-action");
      handleConfirmAction(action, event);

      // Only close confirmation modal if validation passes
      if (validateForm("studentForm")) {
        toggleModal("confirmationModal", false);

        // Close the parent modal (Edit Modal) if open
        const openModal = document.querySelector(
          ".modal[style*='visibility: visible']"
        );
        if (openModal) {
          toggleModal(openModal.id, false);
        }
      }
    }
  });
});

// Function to update grade level

// document.addEventListener("DOMContentLoaded", function () {
//   fetchGradeLevels(); // Call function when page loads
// });

// function fetchGradeLevels() {
//   fetch("/gradelevel/all")
//     .then((response) => {
//       if (!response.ok) {
//         throw new Error("Failed to fetch grade levels");
//       }
//       return response.json();
//     })
//     .then((data) => {
//       const tableBody = document.querySelector("#lamesa tbody");
//       tableBody.innerHTML = ""; // Clear existing rows

//       data.forEach((grade) => {
//         const row = document.createElement("tr");
//         row.innerHTML = `
//                       <td>${grade.levelName}</td>
//                       <td>
//                           <div class="action-container">
//                               <div class="action">
//                                   <img data-open-modal="gradeLevelEditModal"
//                                       src="/images/icons/compose.png"
//                                       alt="grade-level-icon"
//                                       data-id="${grade.levelId}">
//                               </div>
//                           </div>
//                       </td>
//                   `;
//         tableBody.appendChild(row);
//       });
//     })
//     .catch((error) => {
//       console.error("Error:", error);
//     });
// }

// document.addEventListener("click", function (event) {
//   const target = event.target;
//   console.log("Clicked Element:", target); // ✅ Debug element
//   console.log("Element Dataset:", target.dataset); // ✅ Check all dataset attributes

//   if (target.dataset.openModal === "gradeLevelEditModal") {
//     console.log("Target has openModal attribute!"); // ✅ Debug confirmation
//     const gradeLevelId = target.dataset.id;
//     console.log("Grade Level ID:", gradeLevelId); // ✅ Check if ID is present

//     if (!gradeLevelId) {
//       console.error(
//         "Error: gradeLevelId is undefined! Make sure data-id is set in HTML."
//       );
//       return;
//     }

//     fetch(`/gradelevel/${gradeLevelId}`)
//       .then((response) => response.json())
//       .then((data) => {
//         console.log("Fetched Data:", data); // ✅ Check backend response
//         document.getElementById("gradeLevelEditModal").dataset.id = data.id;
//         document.getElementById("levelNameEdit").value = data.levelName; // ✅ Debugging test
//       })
//       .catch((error) => console.error("Error fetching grade level:", error));
//   }
// });
// function editGradeLevel() {
//   const form = document.getElementById("gradeLevelEditForm");
//   const gradeId = form.dataset.id; // Get stored ID

//   const gradeLevelData = {
//     levelId: gradeId,
//     levelName: document.getElementById("levelNameEdit").value,
//   };

//   fetch("/gradelevel/update-grade-level", {
//     method: "PUT",
//     headers: {
//       "Content-Type": "application/json",
//     },
//     body: JSON.stringify(gradeLevelData),
//   })
//     .then((response) => response.text())
//     .then((data) => {
//       alert(data);
//       if (data.includes("Successfully")) {
//         location.reload();
//       }
//     })
//     .catch((error) => console.error("Error:", error));
// }
// document.addEventListener("DOMContentLoaded", function () {
//   // Handle clicking the compose icon
//   document.body.addEventListener("click", function (event) {
//     const target = event.target.closest(
//       "[data-open-modal='gradeLevelEditModal']"
//     ); // Get closest button

//     if (target) {
//       selectedGradeLevelId = target.getAttribute("data-id"); // Get the ID
//       const gradeName = target.getAttribute("data-name"); // Get grade name

//       console.log("Selected Grade Level ID:", selectedGradeLevelId); // Debugging

//       document.getElementById("levelNameEdit").value = gradeName; // Set name
//       document.getElementById("gradeLevelEditForm").dataset.id =
//         selectedGradeLevelId; // Store ID
//     }
//   });
// });

document.addEventListener("DOMContentLoaded", () => {
  const tableBody = document.getElementById("scheduleBody");
  const addRowBtn = document.getElementById("addRowBtn");

  let draggedRow = null;

  // Add button click
  addRowBtn.addEventListener("click", async () => {
    const newRow = await createRow();
    tableBody.appendChild(newRow);
  });

  // function attachDoubleClickDrag(row) {
  //   row.addEventListener("dblclick", () => {
  //     row.setAttribute("draggable", "true");
  //     row.classList.add("dragging-enabled");
  //   });

  //   row.addEventListener("dragstart", (e) => {
  //     if (row.getAttribute("draggable") !== "true") {
  //       e.preventDefault();
  //       return;
  //     }
  //     draggedRow = row;
  //     row.classList.add("dragging");
  //     e.dataTransfer.effectAllowed = "move";
  //   });

  //   row.addEventListener("dragover", (e) => {
  //     e.preventDefault();
  //     const afterElement = getDragAfterElement(tableBody, e.clientY);
  //     if (!afterElement) {
  //       tableBody.appendChild(draggedRow);
  //     } else {
  //       tableBody.insertBefore(draggedRow, afterElement);
  //     }
  //   });

  //   row.addEventListener("dragend", () => {
  //     if (draggedRow) {
  //       draggedRow.classList.remove("dragging");
  //       draggedRow.setAttribute("draggable", "false");
  //       draggedRow.classList.remove("dragging-enabled");
  //       draggedRow = null;
  //     }
  //   });
  // }

  function getDragAfterElement(container, y) {
    const draggableElements = [
      ...container.querySelectorAll("tr.sched-row:not(.dragging)"),
    ];

    return draggableElements.reduce(
      (closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;

        if (offset < 0 && offset > closest.offset) {
          return { offset: offset, element: child };
        } else {
          return closest;
        }
      },
      { offset: Number.NEGATIVE_INFINITY }
    ).element;
  }

  // var selectedSectionId;
  // async function createRow() {
  //   const path = window.location.pathname;
  //   selectedSectionId = path.split("/").pop();
  //   const newRow = document.createElement("tr");
  //   newRow.classList.add("sched-row");
  //   newRow.setAttribute("draggable", "false");

  //   newRow.innerHTML = `
  //       <td>
  //           <select name="subject" class="subject-select">
  //               <option value="" disabled selected>Choose a subject</option>
  //           </select>
  //       </td>
  //       <td>
  //           <select name="teacher" class="teacher-select">
  //               <option value="" disabled selected>Choose a teacher</option>
  //           </select>
  //       </td>
  //       <td>
  //           <select name="days" class="day-select">
  //               <option value="" disabled selected>Choose a day</option>
  //               <option value="SATURDAY">Saturday</option>
  //               <option value="SUNDAY">Sunday</option>
  //           </select>
  //       </td>
  //       <td><input type="time" class="starttime-input"></td>
  //       <td><input type="time" class="endtime-input"></td>
  //     <td>
  //      <button class="btn-row-cancel cancel-btn" title="Cancel">
  //           <img src="/images/icons/cross.png" alt="Cancel" />
  //       </button>
  //       <button class="btn-row-save confirm-btn" title="Save">
  //           <img src="/images/icons/check.png" alt="Save" />
  //       </button>
  //       </td>`;

  //   await populateSubjects(selectedSectionId, newRow);
  //   await populateTeachers(newRow);

  //   attachDoubleClickDrag(newRow);
  //   attachRowActionHandlers(newRow);

  //   return newRow;
  // }

  // window.attachedDeleteHandler = function(row){
  //   const deleteBtn = row.querySelector("#delete-btn");
  //   const editbtn =  row.querySelector("#edit-btn");
  //       deleteBtn.addEventListener('click', function() {
  //           const scheduleId = this.getAttribute('data-id');
  //           fetch(`/schedules/delete/${scheduleId}`, {
  //               method: 'DELETE'
  //           })
  //               .then(response => {
  //                   return response.text().then(message => {
  //                       if (response.ok) {
  //                           showSuccessModal(message,false);
  //                           row.remove();
  //                       } else {
  //                           showErrorModal(message);
  //                       }
  //                   });
  //               })
  //               .catch(error => {
  //                   showErrorModal(error.message);
  //               });
  //               });
  //     editbtn.addEventListener('click', function() {
  //       var oldRow = document.createElement("tr");
  //       oldRow.innerHTML = row.innerHTML;
  //       const scheduleId = this.getAttribute('data-id');
  //       var editSection = this.getAttribute('data-sect-id');
  //       var day = this.getAttribute('data-day');
  //       var subjId = this.getAttribute('data-subj-id');
  //       var teacherId = this.getAttribute('data-teacher-id');
  //       var dayOpt = day === "SATURDAY" ?`
  //                   <option value="SATURDAY" selected>Saturday</option>
  //                   <option value="SUNDAY">Sunday</option>`
  //                   :`
  //                   <option value="SATURDAY">Saturday</option>
  //                   <option value="SUNDAY"selected>Sunday</option>`;
  //       row.innerHTML = `
  //           <td>
  //               <select name="subject" class="subject-select">
  //                   <option value="" disabled selected>Choose a subject</option>
  //               </select>
  //           </td>
  //           <td>
  //               <select name="teacher" class="teacher-select">
  //                   <option value="" disabled selected>Choose a teacher</option>
  //               </select>
  //           </td>
  //           <td>
  //               <select name="days" class="day-select">
  //                   <option value="" disabled selected>Choose a day</option>
  //                   ${dayOpt}
  //               </select>
  //           </td>
  //           <td><input type="time" class="starttime-input"></td>
  //           <td><input type="time" class="endtime-input"></td>
  //         <td>
  //           <button id="cancelEdit">Cancel</button>
  //           <button id="saveEdit" data-sched-id="${scheduleId}">Save Changes</button>
  //         </td>`;
  //       populateSubjects(editSection, row,subjId);
  //       populateTeachers(row,teacherId);
  //       attachedEditHandler(row,oldRow.innerHTML);
  //       });
  // }

  // window.attachedEditHandler = function(row,oldRow){
  //   const cancelEdit = row.querySelector("#cancelEdit");
  //   const saveEdit =  row.querySelector("#saveEdit");

  //   cancelEdit.addEventListener('click', function() {
  //     row.innerHTML=`<tr>${oldRow}</tr>`;
  //     attachedDeleteHandler(row);
  //   });
  //   saveEdit.addEventListener('click', function() {
  //       var scheduleId = this.getAttribute('data-sched-id');
  //       const addSubjectId = row.querySelector(".subject-select").value;
  //       const teacher = row.querySelector(".teacher-select").value;
  //       const day = row.querySelector(".day-select").value;
  //       const startTime = row.querySelector(".starttime-input").value;
  //       const endTime = row.querySelector(".endtime-input").value;


  //       if (!addSubjectId || !teacher || !day || !startTime || !endTime) {
  //           showErrorModal('Please fill in all fields.');
  //           return;
  //       } else if (startTime >= endTime) {
  //           showErrorModal('End time must be after start time.');
  //           return;
  //       }

  //       const path = window.location.pathname;
  //       selectedSectionId = path.split("/").pop();
  //       const data = {
  //           scheduleNumber : scheduleId,
  //           sectionId: selectedSectionId,
  //           subjectId: addSubjectId,
  //           teacherId: teacher,
  //           day: day,
  //           timeStart: startTime,
  //           timeEnd: endTime
  //       };
  //       fetch('/schedules/update', {
  //         method: 'PUT',
  //         headers: {
  //             'Content-Type': 'application/json'
  //         },
  //         body: JSON.stringify(data) // Ensure `data` is defined and contains the required payload
  //     })
  //     .then(response => {
  //       if(response.ok){
  //         return response.json().then(rec => {
  //           showSuccessModal("Schedule updated successfully",false);
  //           row.innerHTML =`
  //           <tr class="sched-row hidden-row">
  //               <td>${rec.subject}</td>
  //               <td>${rec.teacherName}</td>
  //               <td>${rec.day}</td>
  //               <td>${rec.timeStartString}</td>
  //               <td>${rec.timeEndString}</td>
  //               <td>
  //                   <button id="delete-btn" data-id="${rec.scheduleNumber}">Delete</button>
  //                   <img class="delete-row" th:src="@{/images/icons/cross.png}" alt="">
  //                   <button id="edit-btn" data-id="${rec.scheduleNumber}" data-subj-id=${rec.subjectId}
  //                       data-teacher-id=${rec.teacherId} data-sect-id=${rec.sectionId} data-day=${rec.day}
  //                       >Edit This</button>
  //                   <img class="edit-row" th:src="@{/images/icons/cross.png}" alt="">
  //               </td>
  //           </tr>`;
  //           attachedDeleteHandler(row);
  //         });
  //       }else{
  //           return response.text().then(message => {
  //             showErrorModal(message);
  //         });
  //       }
  //     })
  //     .catch(error => {
  //       showErrorModal(error.message);
  //     });
  //   });
  // }

  // function attachRowActionHandlers(row) {
  //   const cancelBtn = row.querySelector(".btn-row-cancel");
  //   const saveBtn = row.querySelector(".btn-row-save");

  //   cancelBtn.addEventListener("click", () => {
  //     row.remove(); // Just removes this row
  //   });

  //   saveBtn.addEventListener("click", () => {
  //     // Grab values from the inputs
  //     const addSubjectId = row.querySelector(".subject-select").value;
  //     const teacher = row.querySelector(".teacher-select").value;
  //     const day = row.querySelector(".day-select").value;
  //     const startTime = row.querySelector(".starttime-input").value;
  //     const endTime = row.querySelector(".endtime-input").value;

  //       // Validate that all fields are filled
  //     if (!addSubjectId || !teacher || !day || !startTime || !endTime) {
  //         showErrorModal('Please fill in all fields.');
  //         return;
  //     } else if (startTime >= endTime) {
  //         showErrorModal('End time must be after start time.');
  //         return;
  //     }

  //     const data = {
  //         sectionId: selectedSectionId,
  //         subjectId: addSubjectId,
  //         teacherId: teacher,
  //         day: day,
  //         timeStart: startTime,
  //         timeEnd: endTime
  //     };
    
  //     fetch('/schedules/add', {
  //         method: 'POST',
  //         headers: {
  //             'Content-Type': 'application/json'
  //         },
  //         body: JSON.stringify(data) // Ensure `data` is defined and contains the required payload
  //     })
  //     .then(response => {
  //       if(response.ok){
  //         return response.json().then(rec => {
  //           showSuccessModal("New schedule added successfully",false);
  //           row.innerHTML =`
  //           <tr class="sched-row hidden-row">
  //               <td>${rec.subject}</td>
  //               <td>${rec.teacherName}</td>
  //               <td>${rec.day}</td>
  //               <td>${rec.timeStartString}</td>
  //               <td>${rec.timeEndString}</td>
  //               <td>
  //                               <button id="delete-btn" data-id="${rec.scheduleNumber}">Delete</button>
  //                               <img class="delete-row" th:src="@{/images/icons/cross.png}" alt="">
  //                               <button id="edit-btn" data-id="${rec.scheduleNumber}" data-subj-id=${rec.subjectId}
  //                                   data-teacher-id=${rec.teacherId} data-sect-id=${rec.sectionId} data-day=${rec.day}
  //                                   >Edit This</button>
  //                               <img class="edit-row" th:src="@{/images/icons/cross.png}" alt="">
  //               </td>
  //           </tr>`;
  //           attachedDeleteHandler(row);
  //         });
  //       }else{
  //           return response.text().then(message => {
  //             showErrorModal(message);
  //         });
  //       }
  //     })
  //     .catch(error => {
  //       showErrorModal(error.message);
  //     });
  //   });
  // }

  // async function populateSubjects(sectionId, row, selectedSub = 0) {
  //   try {
  //     const res = await fetch(`/subject/section/${sectionId}`);
  //     const subjects = await res.json();
  //     const select = row.querySelector(".subject-select");
  //     subjects.forEach((subject) => {
  //       console.log(subject);
  //       const opt = document.createElement("option");
  //       opt.value = subject.subjectNumber;
  //       opt.textContent = subject.subjectName;
  //       if(selectedSub == subject.subjectNumber){
  //         opt.selected = true;
  //       }
  //       select.appendChild(opt);
  //     });
  //   } catch (err) {
  //     console.error("Error loading subjects:", err);
  //   }
  // }

  // async function populateTeachers(row,id = 0) {
  //   try {
  //     const res = await fetch("/user/teachers");
  //     const teachers = await res.json();
  //     const select = row.querySelector(".teacher-select");
  //     teachers.forEach((teacher) => {
  //       const opt = document.createElement("option");
  //       opt.value = teacher.staffId;
  //       if(teacher.staffId == id){
  //         opt.selected = true;
  //       }
  //       opt.textContent = teacher.fullName;
  //       select.appendChild(opt);
  //     });
  //   } catch (err) {
  //     console.error("Error loading teachers:", err);
  //   }
  // }
});

document.addEventListener("DOMContentLoaded", function () {
  const printButton = document.querySelector(".print-btn");
  const saveButton = document.getElementById("save-payment-btn");

  // ✅ Only hide Save button, no need to touch print button
  window.confirmSave = function () {
    if (saveButton) saveButton.style.display = "none";
    if (printButton) {
      printButton.style.display = "inline-block"; // Make sure it shows up
      printButton.textContent = "PRINT RECEIPT"; // Just in case
    }
  };
});

// Function to handle printing
function printReceipt() {
  window.print();
}

function printIndividTransactions() {
  // Get the modal you actually want to print
  const modalToPrint = document.getElementById("individTransactionsPrintModal");

  // Show the print modal temporarily (if it's hidden via display:none)
  modalToPrint.style.display = "block";

  // Clone only the printable area inside the modal
  const printableContent = modalToPrint
    .querySelector(".printable-area-transactions")
    .cloneNode(true);

  // Create a new window to print from
  const printWindow = window.open("", "", "width=800,height=600");

  // Write content into new window
  printWindow.document.write(`
        <html>
            <head>
                <title>Print Record</title>
                <link rel="stylesheet" href="/../css/styles.css">
            </head>
            <body>
                ${printableContent.outerHTML}
            </body>
        </html>
    `);

  printWindow.document.close();

  printWindow.onload = () => {
    printWindow.focus();
    printWindow.print();
  };

  // Re-hide the print modal if it was hidden before
  modalToPrint.style.display = "none";

  // Close the window after printing is done or canceled
  printWindow.onafterprint = () => {
    printWindow.close();
  };
}

function printClaimedResources() {
  // Get the modal you actually want to print
  const modalToPrint = document.getElementById(
    "individClaimedResourcesPrintModal"
  );

  // Show the print modal temporarily (if it's hidden via display:none)
  modalToPrint.style.display = "block";

  // Clone only the printable area inside the modal
  const printableContent = modalToPrint
    .querySelector(".printable-area-claimed-resources")
    .cloneNode(true);

  // Create a new window to print from
  const printWindow = window.open("", "", "width=800,height=600");

  // Write content into new window
  printWindow.document.write(`
          <html>
              <head>
                  <title>Print Record</title>
                  <link rel="stylesheet" href="/../css/styles.css">
              </head>
              <body>
                  ${printableContent.outerHTML}
              </body>
          </html>
      `);

  printWindow.document.close();

  printWindow.onload = () => {
    printWindow.focus();
    printWindow.print();
  };

  // Re-hide the print modal if it was hidden before
  modalToPrint.style.display = "none";

  // Close the window after printing is done or canceled
  printWindow.onafterprint = () => {
    printWindow.close();
  };
}

function clearForm() {
  document
    .querySelectorAll(".modal input, .modal select, .modal textarea")
    .forEach((element) => {
      // Only clear if NOT disabled, NOT readonly, and doesn't have data-preserve
      if (
        !element.isDisabled &&
        !element.readOnly &&
        !element.hasAttribute("data-preserve")
      ) {
        if (element.type === "checkbox" || element.type === "radio") {
          element.checked = false;
        } else {
          element.value = "";
        }
      }
    });
}

// this is for the checkbox in new student modal form
document.addEventListener("DOMContentLoaded", () => {
  const transfereeCheckbox = document.getElementById("isTransferee");
  const transfereeFields = document.getElementById("transfereeFields");
  const transfereeInputs = transfereeFields.querySelectorAll("input");
  if (!transfereeCheckbox || !transfereeFields || !transfereeInputs) return;

  // Toggle visibility and readonly state
  transfereeCheckbox.addEventListener("change", () => {
    if (transfereeCheckbox.checked) {
      transfereeFields.style.display = "block";
      transfereeFields.classList.add("show");

      // Make transferee fields editable
      fetchTransRequirements();
      transfereeInputs.forEach((input) => input.removeAttribute("readonly"));
    } else {
      transfereeFields.classList.remove("show");
      transfereeFields.style.display = "none";

      // Make transferee fields readonly
      transfereeInputs.forEach((input) =>
        input.setAttribute("readonly", "true")
      );
    }
  });
});

// this is key listener escape for forms
document.addEventListener("keydown", function (event) {
  if (event.key === "Escape") {
    const closeButtons = document.querySelectorAll("[data-close-modal]");
    closeButtons.forEach((button) => button.click());
  }
});

// these are checkboxes in the transferee requirements
document.addEventListener("DOMContentLoaded", () => {
  const checkboxes = document.querySelectorAll(
    '.checkbox-each input[type="checkbox"]'
  );
  const updateBtn = document.getElementById("updateRequirementsBtn");

  // Initially disable all checkboxes
  checkboxes.forEach((checkbox) => (checkbox.disabled = true));

  // Toggle function on button click
  updateBtn.addEventListener("click", () => {
    const isDisabled = checkboxes[0].disabled; // Check the current state

    checkboxes.forEach((checkbox) => {
      checkbox.disabled = !isDisabled; // Toggle disabled state
    });

    // Change button label
    updateBtn.textContent = isDisabled ? "Save" : "Update";
  });
});

// This is for for the showing and hiding of the shadow of the sticky-header:
document.addEventListener("DOMContentLoaded", function () {
  const header = document.querySelector(".sticky-header");

  // Function to check scroll position
  function updateShadow() {
    if (window.scrollY > 0) {
      header.classList.add("with-shadow");
      header.classList.remove("no-shadow");
    } else {
      header.classList.add("no-shadow");
      header.classList.remove("with-shadow");
    }
  }

  // Run on page load
  updateShadow();

  // Run on scroll
  window.addEventListener("scroll", updateShadow);
});

// Confirm Button Title
document.addEventListener("DOMContentLoaded", function () {
  document
    .querySelectorAll('button, input[type="button"], input[type="submit"]')
    .forEach(function (btn) {
      const text = btn.value || btn.textContent;
      if (text && text.includes("Confirm")) {
        btn.title = "Confirm Button";
      }
    });
});

// Search Button Title
document.addEventListener("DOMContentLoaded", function () {
  document
    .querySelectorAll('button, input[type="button"], input[type="submit"]')
    .forEach(function (btn) {
      const text = btn.value || btn.textContent;
      if (text && text.includes("Search")) {
        btn.title = "Click this to search after typing the info";
      }
    });
});

// "Main" Href Link Title
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("a").forEach(function (link) {
    if (link.textContent.trim() === "Main") {
      link.title = "Go to Dashboard";
    }
  });
});

// Add Title on each Burger Image Hide Sidebar
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("span.hamburger").forEach(function (span) {
    const img = span.querySelector("img");
    if (img && img.src.includes("burger_menu_icon.png")) {
      span.title = "Hide Sidebar"; // Default title when sidebar is expanded
    }
  });
});

// function toggleSidebar() {
//   let sidebar = document.getElementById("sidebar");
//   let content = document.getElementById("content");

//   const isCollapsed = sidebar.classList.contains("collapsed-sidebar");

//   if (isCollapsed) {
//     sidebar.classList.remove("collapsed-sidebar");
//     content.classList.remove("collapsed-content");
//     content.style.marginLeft = "320px";
//   } else {
//     sidebar.classList.add("collapsed-sidebar");
//     content.classList.add("collapsed-content");
//     content.style.marginLeft = "0";
//   }

//   // Update all hamburger titles after toggling
//   document.querySelectorAll("span.hamburger").forEach(function (span) {
//     const img = span.querySelector("img");
//     if (img && img.src.includes("burger_menu_icon.png")) {
//       span.title = isCollapsed ? "Hide Sidebar" : "Show Sidebar";
//     }
//   });
// }

// Pagination Title Function
document.addEventListener("DOMContentLoaded", function () {
  const prevPageBtn = document.getElementById("prevPage");
  const nextPageBtn = document.getElementById("nextPage");

  // ✅ Only run pagination logic if both buttons exist
  if (!prevPageBtn || !nextPageBtn) return;

  const totalItems = 95; // Can be dynamic
  const itemsPerPage = 10;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  let currentPage = 1;

  updatePaginationTitles(currentPage, totalPages);

  function updatePaginationTitles(currentPage, totalPages) {
    if (currentPage === 1) {
      prevPageBtn.disabled = true;
      prevPageBtn.title = "You're already on the first page.";
    } else {
      prevPageBtn.disabled = false;
      prevPageBtn.title = "Click to show previous page.";
    }

    if (currentPage === totalPages) {
      nextPageBtn.disabled = true;
      nextPageBtn.title = "You're already on the last page.";
    } else {
      nextPageBtn.disabled = false;
      nextPageBtn.title = "Click to show next page.";
    }
  }

  prevPageBtn.addEventListener("click", function () {
    if (currentPage > 1) {
      currentPage--;
      updatePaginationTitles(currentPage, totalPages);
    }
  });

  nextPageBtn.addEventListener("click", function () {
    if (currentPage < totalPages) {
      currentPage++;
      updatePaginationTitles(currentPage, totalPages);
    }
  });
});

// This is for the dropdown buttons (to open, hide, and clicking outisde will close the dropdown)
// document.addEventListener("DOMContentLoaded", function () {
//   // Function to toggle dropdown visibility
//   function toggleDropdown(event) {
//     event.stopPropagation(); // Prevent click from propagating to document

//     const dropdown = event.target.closest(".dropdown");
//     if (!dropdown) return;

//     const dropdownContent = dropdown.querySelector(".dropdown-status-content");
//     if (!dropdownContent) return;

//     // First, close all other dropdowns
//     document.querySelectorAll(".dropdown-status-content").forEach((content) => {
//       if (content !== dropdownContent) {
//         content.style.display = "none";
//       }
//     });

//     // Then toggle the one you clicked
//     dropdownContent.style.display =
//       dropdownContent.style.display === "block" ? "none" : "block";
//   }

//   // Close the dropdown if clicked outside
//   document.addEventListener("click", function (event) {
//     const dropdownContents = document.querySelectorAll(
//       ".dropdown-status-content"
//     );

//     dropdownContents.forEach((dropdownContent) => {
//       if (!dropdownContent.contains(event.target)) {
//         dropdownContent.style.display = "none";
//       }
//     });
//   });

//   // Delegate click event to the parent of all dropdown buttons
//   document.body.addEventListener("click", function (event) {
//     const targetButton = event.target.closest(".dropdown-print-btn");
//     const targetStatusButton = event.target.closest(".dropdown-status-btn");

//     if (targetButton || targetStatusButton) {
//       toggleDropdown(event);
//     }
//   });
// });

document.addEventListener("DOMContentLoaded", function () {
  // Central dropdown handling
  function toggleDropdown(event, dropdownClass, contentClass) {
    event.stopPropagation();

    const dropdown = event.target.closest(`.${dropdownClass}`);
    if (!dropdown) return;

    const dropdownContent = dropdown.querySelector(`.${contentClass}`);
    if (!dropdownContent) return;

    // Close all other dropdowns
    document.querySelectorAll(`.${contentClass}`).forEach((content) => {
      if (content !== dropdownContent) {
        content.style.display = "none";
      }
    });

    // Toggle current
    dropdownContent.style.display =
      dropdownContent.style.display === "block" ? "none" : "block";
  }

  // Close any dropdown if clicked outside
  document.addEventListener("click", function (event) {
    document
      .querySelectorAll(
        ".dropdown-status-content, .add-something-dropdown-content"
      )
      .forEach((dropdownContent) => {
        if (!dropdownContent.contains(event.target)) {
          dropdownContent.style.display = "none";
        }
      });
  });

  // Listen for clicks on dropdown buttons
  document.body.addEventListener("click", function (event) {
    const printBtn = event.target.closest(".dropdown-print-btn");
    const statusBtn = event.target.closest(".dropdown-status-btn");
    const addSomethingBtn = event.target.closest(".add-something-btn");

    if (printBtn) {
      toggleDropdown(event, "dropdown", "dropdown-status-content");
    } else if (statusBtn) {
      toggleDropdown(event, "dropdown", "dropdown-status-content");
    } else if (addSomethingBtn) {
      toggleDropdown(
        event,
        "add-something-dropdown",
        "add-something-dropdown-content"
      );
    }
  });
});

//  This code is for scroling in the sidebar issue
document.addEventListener("DOMContentLoaded", function () {
  const modal = document.querySelector(".sidebar");

  sidebar.addEventListener(
    "wheel",
    function (e) {
      const atTop = sidebar.scrollTop === 0;
      const atBottom =
        Math.abs(
          sidebar.scrollHeight - sidebar.scrollTop - sidebar.clientHeight
        ) < 1;

      if (
        (e.deltaY < 0 && atTop) || // scrolling up at top
        (e.deltaY > 0 && atBottom) // scrolling down at bottom
      ) {
        e.preventDefault(); // prevent scroll bubbling
      }
    },
    { passive: false }
  ); // passive: false is important to allow preventDefault()
});

document.addEventListener("DOMContentLoaded", function () {
  const scrollableElements = document.querySelectorAll(".scroll-lock");

  scrollableElements.forEach((el) => {
    el.addEventListener(
      "wheel",
      function (e) {
        const atTop = el.scrollTop === 0;
        const atBottom =
          Math.abs(el.scrollHeight - el.scrollTop - el.clientHeight) < 1;

        if ((e.deltaY < 0 && atTop) || (e.deltaY > 0 && atBottom)) {
          e.preventDefault();
        }
      },
      { passive: false }
    );
  });
});
// document.addEventListener("DOMContentLoaded", function () {
//   const toggle = document.getElementById("toggleResetPassword"); // updated ID
//   const passwordField = document.getElementById("password");
//   let isVisible = false;

//   if (!toggle || !passwordField) {
//     console.warn("Toggle icon or password field not found.");
//     return;
//   }

//   toggle.addEventListener("click", function () {
//     isVisible = !isVisible;

//     passwordField.type = isVisible ? "text" : "password";

//     toggle.src = isVisible
//       ? "/images/icons/eye.png"
//       : "/images/icons/hidden-pass.png";

//     toggle.title = isVisible ? "Hide Password" : "Show Password";
//     toggle.alt = isVisible ? "Hide Password" : "Show Password";
//   });
// });

// document.querySelectorAll('.dropdown-print-btn').forEach(button => {
//     button.addEventListener('click', (event) => {
//         const dropdownContent = event.target.closest('.dropdown').querySelector('.dropdown-status-content');
//         const dropdownButton = event.target;
//         const rect = dropdownButton.getBoundingClientRect();
//         const windowHeight = window.innerHeight;

//         // Calculate the available space below the button
//         const spaceBelow = windowHeight - rect.bottom;

//         // If there's not enough space below, position the dropdown above
//         if (spaceBelow < dropdownContent.offsetHeight) {
//             dropdownContent.classList.add('above'); // Position above
//         } else {
//             dropdownContent.classList.remove('above'); // Default position below
//         }

//         // Toggle the dropdown visibility
//         dropdownContent.style.display = dropdownContent.style.display === 'block' ? 'none' : 'block';
//         dropdownContent.style.opacity = dropdownContent.style.opacity === '1' ? '0' : '1';
//     });
// });;

// // This is for the popover so clicking it will open and clicking it again will close the popover
// document.addEventListener("DOMContentLoaded", function () {
//   const button = document.getElementById("dropdown-scholar");
//   const popover = document.getElementById("my-popover");

//   // Toggle popover visibility when button is clicked
//   button.addEventListener("click", function (event) {
//     // Prevent the event from propagating to the document click listener
//     event.stopPropagation();

//     if (popover.style.visibility === "visible") {
//       // Hide the popover
//       popover.style.visibility = "hidden";
//       popover.style.opacity = "0";
//       popover.style.pointerEvents = "none"; // Disable interaction
//     } else {
//       // Show the popover
//       popover.style.visibility = "visible";
//       popover.style.opacity = "1";
//       popover.style.pointerEvents = "auto"; // Enable interaction
//     }
//   });

//   // Close the popover when clicking anywhere outside the popover or button
//   document.addEventListener("click", function () {
//     if (popover.style.visibility === "visible") {
//       // Hide the popover if it's visible
//       popover.style.visibility = "hidden";
//       popover.style.opacity = "0";
//       popover.style.pointerEvents = "none"; // Disable interaction
//     }
//   });
// });
// document.addEventListener("DOMContentLoaded", function () {
//     const button = document.getElementById("dropdown-scholar");
//     const popover = document.getElementById("my-popover");
//     const modalContent = document.querySelector(".student-modal-content");

//     // Validate elements
//     if (!button || !popover || !modalContent) {
//       console.error("Missing elements:", { button, popover, modalContent });
//       return;
//     }

//     function positionPopover() {
//       // Use offsetTop for reliability
//       const buttonTop = button.offsetTop;
//       const buttonLeft = button.offsetLeft;
//       const buttonHeight = button.offsetHeight;
//       const scrollTop = modalContent.scrollTop;

//       // Position below button, adjust for scroll
//       const top = buttonTop + buttonHeight + 5 - scrollTop;
//       const left = buttonLeft;

//       // Apply position
//       popover.style.top = `${top}px`;
//       popover.style.left = `${left}px`;

//       // Force render (helps with browser repaint issues)
//       popover.style.display = "none";
//       popover.offsetHeight; // Trigger reflow
//       popover.style.display = "";

//       // Debug log
//       console.log("Popover positioned:", {
//         top,
//         left,
//         scrollTop,
//         buttonTop,
//         computedTop: popover.style.top,
//       });
//     }

//     button.addEventListener("click", function (e) {
//       e.stopPropagation();
//       const isVisible = popover.style.visibility === "visible";
//       if (isVisible) {
//         popover.style.visibility = "hidden";
//         popover.style.opacity = "0";
//         popover.style.pointerEvents = "none";
//       } else {
//         positionPopover();
//         popover.style.visibility = "visible";
//         popover.style.opacity = "1";
//         popover.style.pointerEvents = "auto";
//       }
//     });

//     // Handle modal scroll
//     modalContent.addEventListener("scroll", () => {
//       console.log("Modal scroll, scrollTop:", modalContent.scrollTop);
//       if (popover.style.visibility === "visible") {
//         positionPopover();
//       }
//     }, { passive: true });

//     // Handle window scroll (for outside modal)
//     window.addEventListener("scroll", () => {
//       if (popover.style.visibility === "visible") {
//         positionPopover();
//       }
//     });

//     // Close popover on outside click
//     document.addEventListener("click", (e) => {
//       if (
//         popover.style.visibility === "visible" &&
//         !popover.contains(e.target) &&
//         !button.contains(e.target)
//       ) {
//         popover.style.visibility = "hidden";
//         popover.style.opacity = "0";
//         popover.style.pointerEvents = "none";
//       }
//     });
//   });

document.addEventListener("DOMContentLoaded", function () {
  const buttons = [
    { buttonId: "dropdown-scholar", popoverId: "scholar-popover" },
    {
      buttonId: "dropdown-report-scholar",
      popoverId: "report-scholar-popover",
    },
    {
      buttonId: "dropdown-report-transferee",
      popoverId: "report-transferee-popover",
    },
    { buttonId: "dropdown-add-scholar", popoverId: "add-scholar-popover" },
    { buttonId: "dropdown-transferee", popoverId: "transferee-popover" },
    {
      buttonId: "dropdown-edit-transferee",
      popoverId: "edit-transferee-popover",
    },
    { buttonId: "dropdown-edit-scholar", popoverId: "edit-scholar-popover" },
    {
      buttonId: "dropdown-add-transferee",
      popoverId: "add-transferee-popover",
    },
  ];

  // Validate modal and popovers
  if (
    !document.querySelector(
      ".student-modal-content, .student-information-container-content"
    )
  ) {
    return;
  }

  // Initialize both popovers to hidden on page load
  buttons.forEach(({ popoverId }) => {
    const popover = document.getElementById(popoverId);
    if (popover) {
      popover.style.visibility = "hidden";
      popover.style.opacity = "0";
      popover.style.pointerEvents = "none";
    }
  });

  // Offset to move popovers lower and right
  const popoverOffset = -5; // Adjust this value to move the popover down more
  const popoverLeftOffset = 10; // Adjust this value to move the popover to the left of the button

  function closePopover(popover) {
    if (popover) {
      popover.style.visibility = "hidden";
      popover.style.opacity = "0";
      popover.style.pointerEvents = "none";
    }
  }

  function togglePopover(button, popover) {
    const isVisible =
      popover.style.visibility === "visible" || popover.style.visibility === "";

    // Close all other popovers first
    buttons.forEach(({ popoverId }) => {
      const otherPopover = document.getElementById(popoverId);
      if (otherPopover !== popover) {
        closePopover(otherPopover);
      }
    });

    // Then toggle the current one
    if (isVisible) {
      closePopover(popover);
    } else {
      openPopover(popover);
      positionPopover(button, popover);
    }
  }

  function openPopover(popover) {
    popover.style.visibility = "visible";
    popover.style.opacity = "1";
    popover.style.pointerEvents = "auto";
  }

  function positionPopover(button, popover) {
    // Get button position and dimensions
    const buttonTop = button.offsetTop;
    const buttonLeft = button.offsetLeft;
    const buttonHeight = button.offsetHeight;

    // Get modal and popover dimensions
    const modalContent = document.querySelector(
      ".student-modal-content, .student-information-container-content"
    );
    const modalHeight = modalContent.clientHeight;
    const popoverHeight = popover.offsetHeight || 100; // Fallback if not rendered
    const popoverWidth = popover.offsetWidth || 200; // Assuming width is calculated after render, fallback to 200px

    // Calculate ideal top position (below button)
    let top = buttonTop + buttonHeight + 5 + popoverOffset;
    let position = "below";

    // Check if placing below would exceed the modal's bottom
    if (top + popoverHeight > modalHeight) {
      top = buttonTop - popoverHeight - 5; // Place it above if it exceeds
      position = "above";
    }

    // Boundary check: hide popover if it would go above the modal's top
    if (top < 0) {
      closePopover(popover);
      position = "hidden-top";
    } else {
      // Ensure popover is visible if within bounds
      if (popover.style.visibility !== "visible") {
        openPopover(popover);
      }
      popover.style.pointerEvents = "auto";
    }

    // Adjust position to be to the left of the button
    popover.style.top = `${top}px`;
    popover.style.left = `${buttonLeft - popoverWidth + popoverLeftOffset}px`; // Adjust left to move to the left of the button
  }

  // Open popover when any button is clicked
  buttons.forEach(({ buttonId, popoverId }) => {
    const button = document.getElementById(buttonId);
    const popover = document.getElementById(popoverId);

    if (!button || !popover) return;

    button.addEventListener("click", function (e) {
      e.stopPropagation();
      togglePopover(button, popover);
    });
  });

  // Close popover on outside click (with slight delay to allow button click logic to finish)
  document.addEventListener("mousedown", (e) => {
    buttons.forEach(({ popoverId, buttonId }) => {
      const popover = document.getElementById(popoverId);
      const button = document.getElementById(buttonId);

      // ✅ Check if elements exist first
      if (!popover || !button) return;

      if (
        popover.style.visibility === "visible" &&
        !popover.contains(e.target) &&
        !button.contains(e.target)
      ) {
        closePopover(popover);
      }
    });
  });
});

//This is for Modal Buttons (Cancel and Confirm)
document.addEventListener("DOMContentLoaded", function () {
  const modalButtonsContainers = document.querySelectorAll(
    ".modal-buttons, .confirmation-modal-buttons, .basic-modal-buttons"
  );

  modalButtonsContainers.forEach((container) => {
    const cancelBtn = container.querySelector(
      ".btn-cancel, .btn-close-confirm, .error-btn-cancel"
    );
    const confirmBtn = container.querySelector(".btn-confirm");

    const parentModal = container.closest(".modal");
    const isConfirmation =
      parentModal && parentModal.id === "confirmationModal";
    const isErrorModal = parentModal && parentModal.id === "errorModal";

    // Apply base styles
    container.style.display = "flex";
    container.style.flexWrap = "wrap";
    container.style.width = "100%";
    container.style.marginTop = "1rem";

    // Alignment logic
    if (isErrorModal) {
      container.style.justifyContent = "center";
    } else {
      container.style.justifyContent = isConfirmation ? "center" : "flex-end";
    }

    // Gap logic
    const updateGap = () => {
      if (isConfirmation) {
        container.style.columnGap = window.innerWidth <= 480 ? "32px" : "40px";
      } else {
        container.style.columnGap = window.innerWidth <= 480 ? "16px" : "24px";
      }
    };
    updateGap();
    window.addEventListener("resize", updateGap);

    // Button reordering: Cancel on the left, Confirm on the right (if both buttons exist)
    if (
      !isErrorModal &&
      cancelBtn &&
      confirmBtn &&
      cancelBtn.nextElementSibling !== confirmBtn
    ) {
      container.insertBefore(cancelBtn, confirmBtn);
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const requiredInputs = document.querySelectorAll(
    ".modal input[required], .modal select[required]"
  );

  requiredInputs.forEach((input) => {
    // Listen for blur (when the user leaves the field)
    input.addEventListener("blur", () => {
      validateField(input);
    });

    // Listen for change in select or input fields (when the user selects a value or types)
    input.addEventListener("input", () => {
      validateField(input);
    });

    // Listen for focus (to remove the error when the field is focused again)
    input.addEventListener("focus", () => {
      input.classList.remove("input-error");
      const wrapper = input.closest(".input-wrapper");
      wrapper?.classList.remove("input-error");
      input.removeAttribute("title");
    });
  });

  // Validate function for input and select
  function validateField(input) {
    const wrapper = input.closest(".input-wrapper");

    if (input.tagName === "SELECT" && input.value === "") {
      input.classList.add("input-error");
      wrapper?.classList.add("input-error");
      input.setAttribute("title", "This field is required");
    } else if (input.value.trim() === "") {
      input.classList.add("input-error");
      wrapper?.classList.add("input-error");
      input.setAttribute("title", "This field is required");
    } else {
      input.classList.remove("input-error");
      wrapper?.classList.remove("input-error");
      input.removeAttribute("title");
    }
  }

  // Form submission validation for all required fields
  document.getElementById("confirmStudent").addEventListener("click", () => {
    requiredInputs.forEach((input) => {
      validateField(input);
    });
  });
});

function resetValidationErrors() {
  const requiredInputs = document.querySelectorAll(
    ".modal input[required], .modal select[required]"
  );

  requiredInputs.forEach((input) => {
    input.classList.remove("input-error");
    const wrapper = input.closest(".input-wrapper");
    wrapper?.classList.remove("input-error");
    input.removeAttribute("title");
  });
}
// Center Close Button
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".modal-buttons").forEach(function (container) {
    const btn = container.querySelector("button.btn-cancel");
    if (btn && btn.textContent.trim().toLowerCase() === "close") {
      container.style.display = "flex";
      container.style.justifyContent = "center";
      container.style.alignItems = "center";
    }
  });
});
//GradeLevelAssessment Modal Cancel-Confirm Button Centered
document.addEventListener("DOMContentLoaded", function () {
  const modal = document.querySelector("#addGradeLevelAssessmentModal");
  if (modal) {
    const buttonsContainer = modal.querySelector(".modal-buttons");
    if (buttonsContainer) {
      buttonsContainer.style.display = "flex";
      buttonsContainer.style.justifyContent = "center";
      buttonsContainer.style.alignItems = "center";
      buttonsContainer.style.flexWrap = "wrap";
      buttonsContainer.style.gap = "24px";
      buttonsContainer.style.marginTop = "1rem";
      buttonsContainer.style.width = "100%";
    }
  }
});
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('a[title="You\'re currently in this page."]').forEach(anchor => {
    // 1. Wrap text content in <strong>
    const strong = document.createElement('strong');
    strong.textContent = anchor.textContent;
    anchor.textContent = '';
    anchor.appendChild(strong);

    // 2. Change href to "#"
    anchor.setAttribute('href', '#');
  });
});
