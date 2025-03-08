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

function toggleSidebar() {
  let sidebar = document.getElementById("sidebar");
  let content = document.getElementById("content");

  if (sidebar.classList.contains("collapsed-sidebar")) {
    sidebar.classList.remove("collapsed-sidebar");
    content.classList.remove("collapsed-content");
    content.style.marginLeft = "320px";
  } else {
    sidebar.classList.add("collapsed-sidebar");
    content.classList.add("collapsed-content");
    content.style.marginLeft = "0";
  }
}

// Function to toggle the dropdown menu
function toggleDropdown(id) {
  const dropdownMenu = document.getElementById(id);

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
  event.preventDefault(); // Prevent default anchor behavior

  let submenu = document.getElementById(submenuId);
  let arrowIconImg =
    submenu.previousElementSibling.querySelector(".arrow-icon img");
  let isOpen = submenu.classList.contains("open");

  // Close all unrelated submenus
  document.querySelectorAll(".submenu").forEach((otherSubmenu) => {
    if (
      !submenu.contains(otherSubmenu) &&
      otherSubmenu !== submenu &&
      !otherSubmenu.contains(submenu)
    ) {
      otherSubmenu.classList.remove("open");

      // Reset arrow icons for closed submenus
      let otherArrowIcon =
        otherSubmenu.previousElementSibling?.querySelector(".arrow-icon img");
      if (otherArrowIcon) {
        otherArrowIcon.src =
          //   "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png";
          // otherArrowIcon.src = "../static/images/icons/arrow-down.png";

          otherArrowIcon.src = "/images/icons/arrow-down.png";
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);

  arrowIconImg.src = isOpen
    ? "/images/icons/arrow-down.png" // Change back to down arrow if closing
    : "/images/icons/greater-than.png"; // Change to right arrow if opening

  let allOpenSubmenus = [...document.querySelectorAll(".submenu.open")].map(
    (sub) => sub.id
  );
  localStorage.setItem("openedSubmenus", JSON.stringify(allOpenSubmenus));
}

function clearSubmenus() {
  localStorage.removeItem("openedSubmenus"); // Clear saved submenus
  document
    .querySelectorAll(".submenu")
    .forEach((submenu) => submenu.classList.remove("open"));
  document
    .querySelectorAll(".arrow-icon img")
    // .forEach((img) => (img.src = "../images/icons/arrow-down.png"));
    .forEach((img) => (img.src = "/images/icons/arrow-down.png"));
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

  // Hide modal when page loads
  //   modal.style.display = "none";

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
      } else if (action === "archive") {
        modalText.textContent =
          "Are you sure you want to archive this school year? Take note, you cannot access this schoole year once it is already in your archive";
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

document.addEventListener("DOMContentLoaded", function () {
    function toggleModal(modalId, show = true, message = "") {
        const modal = document.getElementById(modalId);
        if (!modal) return;
    
        if (show) {
            modal.classList.add("show");
            modal.style.visibility = "visible";
            modal.style.opacity = "1";
            modal.style.pointerEvents = "auto"; // Enable interaction
        } else {
            modal.classList.remove("show");
            modal.style.visibility = "hidden";
            modal.style.opacity = "0";
            modal.style.pointerEvents = "none"; // Ensure it doesn't block clicks
        }
    
        // If it's a confirmation modal, update the text
        if (modalId === "confirmationModal" && message) {
            document.getElementById("modalText").textContent = message;
    
            // Reset confirm button properly to avoid event listener issues
            let confirmBtn = document.getElementById("confirmAction");
            let newConfirmBtn = confirmBtn.cloneNode(true);
            confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
    
            // Reattach event listener
            newConfirmBtn.addEventListener("click", function () {
                const action = newConfirmBtn.getAttribute("data-confirm-action");
                handleConfirmAction(action, event);
            });
        }
    }
    


  async function handleConfirmAction(action, event) {
    if (event && typeof event.preventDefault === "function") {
      event.preventDefault();
    } else {
      console.warn("⚠️ Warning: Event is missing or invalid");
    }

    console.log(action);

    if (!action) {
      console.error("❌ Error: Action is undefined!");
      return;
    }

    // ✅ Always close confirmationModal FIRST
    let confirmationModal = document.getElementById("confirmationModal");
    confirmationModal.style.visibility = "hidden";
    confirmationModal.style.opacity = "0";

    console.log("🚀 Handling Action:", action);
    switch (action) {
      case "addNewStudent":
        alert("Add new student!");
        break;
      case "addGradeLevel":
        addGradeLevel();
        break;
      case "editGradeLevel":
        editGradeLevel();
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
      case "addStudent":
        actionUrl = "/student/add";
        method = "POST";
        formData.append(
          "studentName",
          document.getElementById("studentName").value
        );
        break;
      case "addSection":
        addSection();
        break;
      case "editSection":
        editSection();
        break;
      case "deleteSection":
        deleteSection(selectedSectionId);
        break;
      case "addSubject":
        addSubject();
        break;
      case "editSubject":
        editSubject();
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
        verifyAccount(selectedVerificationId);
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
        if (validateSchoolYear()) {
          addSchoolYear();
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
      case "addListing":
        if (!validateForm("studentForm")) {
          console.error(
            "❌ Validation failed: Please fill in all required fields."
          );
          showErrorModal("⚠️ Please fill in all required fields!"); // ✅ Show error modal
          return; // 🚀 Stop execution, keep studentForm open
        }

        // ✅ If valid, proceed & force close confirmationModal
        addListing();
        document.getElementById("confirmationModal").classList.remove("show"); // ✅ Hide it
        document.getElementById("confirmationModal").style.visibility =
          "hidden";
        document.getElementById("confirmationModal").style.opacity = "0";

        break;

      case "finishSemester":
        finishSemester();
        break;
      case "proceedAssessment":
        console.log(enrollmentIdLet);
        proceedToAssessment(enrollmentIdLet);
        break;
      case "proceedToPayment":
        proceedToPayment(enrollmentIdLet, sectionNumberLet);
        break;
      case "proceedToEnrolled":
        proceedToEnrolled(enrollmentIdLet);
      case "addFee":
        addFee();
        break;
      case "editFee":
        editFee();
        break;
      case "savePayment":
        savePayment();
        break;
      default:
        alert("Unknown action: " + action);
        return;
    }
  }
  // ✅ Prevent modal from closing when clicking outside
  document.addEventListener("click", function (event) {
    const studentForm = document.getElementById("studentForm");
    const confirmationModal = document.getElementById("confirmationModal");

    if (
      studentForm &&
      !studentForm.contains(event.target) &&
      event.target !== studentForm
    ) {
      console.log("⛔ Clicked outside studentForm, preventing close...");
      event.stopPropagation(); // ✅ Prevent closing when clicking outside
    }

    if (
      confirmationModal &&
      !confirmationModal.contains(event.target) &&
      event.target !== confirmationModal
    ) {
      console.log("⛔ Clicked outside confirmationModal, preventing close...");
      event.stopPropagation(); // ✅ Prevent closing when clicking outside
    }
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
        input.classList.add("error"); // Add a red border or something
      } else {
        input.classList.remove("error");
      }
    });

    return valid;
  }

  function showErrorModal(message) {
    console.log("🟠 Showing error modal with message:", message);

    const errorModal = document.getElementById("errorModal");
    const errorMessage = document.getElementById("errorMessage");

    if (errorModal && errorMessage) {
      errorMessage.textContent = message; // ✅ Update error message

      // ✅ Remove .show and force reflow before adding it again
      errorModal.classList.remove("show");
      void errorModal.offsetWidth; // 🔥 Forces reflow to restart animation
      errorModal.classList.add("show");

      // Ensure visibility and opacity are reset
      errorModal.style.visibility = "visible";
      errorModal.style.opacity = "1";
    } else {
      alert(`⚠️ ${message}`); // Fallback if modal is missing
    }
  }

  document.querySelectorAll("[data-close-modal]").forEach((btn) => {
    btn.addEventListener("click", function () {
      let modalId = this.getAttribute("data-close-modal");
      let modal = document.getElementById(modalId);

      if (modal) {
        modal.classList.remove("show"); // ✅ Hide modal visually
        modal.style.visibility = "hidden";
        modal.style.opacity = "0";
        modal.style.pointerEvents = "none"; // 🔥 Prevent it from blocking clicks
      }
    });
  });

  document
    .querySelectorAll("[data-close-modal='errorModal']")
    .forEach((btn) => {
      btn.addEventListener("click", function () {
        let errorModal = document.getElementById("errorModal");
        let studentForm = document.getElementById("studentForm");

        // ✅ Hide error modal only
        errorModal.style.visibility = "hidden";
        errorModal.style.opacity = "0";

        // ✅ Ensure confirmationModal STAYS CLOSED
        let confirmationModal = document.getElementById("confirmationModal");
        confirmationModal.style.visibility = "hidden";
        confirmationModal.style.opacity = "0";

        // ✅ Keep studentForm open
        studentForm.style.visibility = "visible";
        studentForm.style.opacity = "1";
      });
    });

  document
    .getElementById("studentForm")
    .addEventListener("submit", function (event) {
      event.preventDefault(); // ✅ Stops form submission
      console.log("⛔ Form submission prevented!");
    });

  document.body.addEventListener("click", function (event) {
    const target = event.target;
    let saveBtn = document.getElementById("saveBtn");

    // Open modal only if saveBtn's text is "Save"
    if (target.matches("[data-open-modal]")) {
      const modalId = target.getAttribute("data-open-modal");
      const message = target.getAttribute("data-message") || "";
      const action = target.getAttribute("data-action") || ""; // Get action

      document
        .getElementById("confirmAction")
        .setAttribute("data-confirm-action", action); // Store action

      // Check if the clicked button is the saveBtn
      if (target === saveBtn) {
        if (saveBtn.textContent.trim() === "Add") {
          console.log("🔄 Changing text to Save...");
          saveBtn.textContent = "Save"; // Change text to Save
          return; // Stop execution so modal does NOT open
        }
      }

      // Open modal only if saveBtn is already "Save"
      if (target.matches("[data-open-modal]")) {
        // if (saveBtn.textContent.trim() === "Save") {
        //   console.log("✅ Showing Modal");
        //   const modalId = target.getAttribute("data-open-modal");
        //   const message = target.getAttribute("data-message") || "";
        //   toggleModal(modalId, true, message);
        // } else {
        //     toggleModal(modalId, true, message);
        // //   console.log(
        // //     "❌ Modal won't open because text is:",
        // //     saveBtn.textContent.trim()
        // //   );
        // }
        const modalId = target.getAttribute("data-open-modal");
        const message = target.getAttribute("data-message") || "";
        toggleModal(modalId, true, message);
      }
    }

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
          }
        });

        target.textContent = "Update";
        cancelBtn.textContent = "Cancel";
        target.setAttribute("data-mode", "update");
      } else if (target.getAttribute("data-mode") === "update") {
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
      const action = target.getAttribute("data-confirm-action"); // ✅ Get action
      handleConfirmAction(action, event); // ✅ Pass event properly

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
  const saveBtn = document.getElementById("saveBtn");
  const clearBtn = document.getElementById("clearBtn");

  let firstClick = true; // Track first click on "Add"
  let draggedRow = null; // For drag-and-drop functionality
  let hasInitialRows = tableBody.children.length > 0; // Check if rows existed on page load

  // Hide buttons initially
  addRowBtn.style.display = "none";
  clearBtn.style.display = "none";
  saveBtn.textContent = hasInitialRows ? "Edit" : "Add"; // Set button based on initial rows

  function updateButtonState() {
    const visibleRows = Array.from(tableBody.children).filter(
      (row) => !row.classList.contains("hidden-row")
    );
    const hasVisibleRows = visibleRows.length > 0;

    if (!hasVisibleRows) {
      saveBtn.textContent = "Add";
      firstClick = true;
      addRowBtn.style.display = "none";
      clearBtn.style.display = "none";
    } else {
      saveBtn.textContent = "Save"; // Keep it "Save" when rows exist
      addRowBtn.style.display = "block"; // Keep the Add Row button visible
      clearBtn.style.display = "block"; // Keep the Clear button visible
    }
  }

  function createRow() {
    const newRow = document.createElement("tr");
    newRow.classList.add("sched-row");
    newRow.setAttribute("draggable", "false"); // Default: Not draggable
    newRow.innerHTML = `
            <td>
                <select name="subject">
                    <option value="" disabled selected>Choose a subject</option>
                </select>
            </td>
            <td>
                <select name="teacher">
                    <option value="" disabled selected>Choose a teacher</option>
                </select>
            </td>
            <td>
                <select name="days">
                    <option value="" disabled selected>Choose a day</option>
                    <option value="Saturday">Saturday</option>
                    <option value="Sunday">Sunday</option>
                </select>
            </td>
            <td>
                <input type="time">
            </td>
            <td>
                <img class="delete-row" src="/images/icons/cross.png" alt="Delete" style="display: inline-block; cursor: pointer;">
            </td>
        `;
    attachDeleteEvent(newRow);
    attachDoubleClickDrag(newRow);
    return newRow;
  }

  function attachDeleteEvent(row) {
    row.querySelector(".delete-row").addEventListener("click", () => {
      row.remove();
      updateButtonState();
    });
  }

  function attachDoubleClickDrag(row) {
    row.addEventListener("dblclick", () => {
      row.setAttribute("draggable", "true"); // Enable dragging on double-click
      row.classList.add("dragging-enabled");
    });

    row.addEventListener("dragstart", (e) => {
      if (row.getAttribute("draggable") !== "true") {
        e.preventDefault();
        return;
      }
      draggedRow = row;
      row.classList.add("dragging");
      e.dataTransfer.effectAllowed = "move";
    });

    row.addEventListener("dragover", (e) => {
      e.preventDefault();
      const afterElement = getDragAfterElement(tableBody, e.clientY);
      if (afterElement == null) {
        tableBody.appendChild(draggedRow);
      } else {
        tableBody.insertBefore(draggedRow, afterElement);
      }
    });

    row.addEventListener("dragend", () => {
      draggedRow.classList.remove("dragging");
      draggedRow.setAttribute("draggable", "false"); // Disable dragging after release
      draggedRow.classList.remove("dragging-enabled");
      draggedRow = null;
    });
  }

  function getDragAfterElement(container, y) {
    const draggableElements = [
      ...container.querySelectorAll(".sched-row:not(.dragging)"),
    ];

    return draggableElements.reduce(
      (closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;
        return offset < 0 && offset > closest.offset
          ? { offset, element: child }
          : closest;
      },
      { offset: Number.NEGATIVE_INFINITY }
    ).element;
  }

  saveBtn.addEventListener("click", () => {
    const visibleRows = Array.from(tableBody.children).filter(
      (row) => !row.classList.contains("hidden-row")
    );
    const hasVisibleRows = visibleRows.length > 0;

    if (firstClick) {
      // First time clicking "Add"
      tableBody.appendChild(createRow());
      addRowBtn.style.display = "block";
      clearBtn.style.display = "block";
      firstClick = false;
    } else {
      addRowBtn.style.display = "block";
      clearBtn.style.display = "block";
    }

    document
      .querySelectorAll(".delete-row")
      .forEach((btn) => (btn.style.display = "inline-block"));
  });
  //old
  //   addRowBtn.addEventListener("click", () => {
  //     tableBody.appendChild(createRow());
  //     updateButtonState();
  //     // Ensure button remains "Save" when adding more rows
  //     saveBtn.textContent = "Save"; // Force it to remain "Save"
  //   });

  //new
  addRowBtn.addEventListener("click", () => {
    tableBody.appendChild(createRow());
    saveBtn.textContent = "Save";
    addRowBtn.style.display = "block"; // Ensure it stays visible
    clearBtn.style.display = "block"; // Ensure clear button stays visible
  });

  //   clearBtn.addEventListener("click", () => {
  //     tableBody.innerHTML = ""; // Clears everything
  //     saveBtn.textContent = "Add";
  //     firstClick = true;
  //     addRowBtn.style.display = "none";
  //     clearBtn.style.display = "none";
  //   });

  window.clearSched = function () {
    tableBody.innerHTML = ""; // Clears everything
    saveBtn.textContent = "Add";
    firstClick = true;
    addRowBtn.style.display = "none";
    clearBtn.style.display = "none";
  };
  //   window.updateAfterSched = function () {
  //     addRowBtn.style.display = "none";
  //     clearBtn.style.display = "none";
  //   };
  updateButtonState();
});

document.addEventListener("DOMContentLoaded", function () {
  const printButton = document.querySelector(
    ".no-print[onclick='printReport()']"
  );
  const saveButton = document.getElementById("save-payment-btn");
  const confirmActionButton = document.getElementById("confirmAction");

  // Hide the print button initially
  printButton.style.display = "none";

  // Function to handle confirmed save action
  window.confirmSave = function () {
    saveButton.style.display = "none"; // Hide Save button
    printButton.style.display = "inline-block"; // Show Print button
  };
});

// Function to handle printing
function printReport() {
  window.print();
}

document.addEventListener("DOMContentLoaded", function () {
  const passwordField = document.getElementById("password");
  const toggleIcon = document.getElementById("togglePassword");

  if (!passwordField || !toggleIcon) {
    console.error("Password field or toggle icon not found!");
    return;
  }

  toggleIcon.addEventListener("click", function () {
    if (passwordField.type === "password") {
      passwordField.type = "text"; // Show password
      toggleIcon.src = "/images/icons/eye.png"; // Open eye icon
      toggleIcon.alt = "Hide Password"; // Update alt text
    } else {
      passwordField.type = "password"; // Hide password
      toggleIcon.src = "/images/icons/hidden-pass.png"; // Closed eye icon
      toggleIcon.alt = "Show Password"; // Update alt text
    }
  });
});
