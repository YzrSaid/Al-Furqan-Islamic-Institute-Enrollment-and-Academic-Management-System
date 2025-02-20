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
          "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png";
        //otherArrowIcon.src = "../static/images/icons/arrow-down.png";

        //   otherArrowIcon.src = "../images/icons/arrow-down.png";
        // ("/testingLogIn/src/main/resources/static/images/icons/arrow-down.png");
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);

  // *🔹 Save submenu state only if it is a submenu of a submenu*
  arrowIconImg.src = isOpen
    ? //   ? "../images/icons/arrow-down.png" // Change back to down arrow if closing
      //   : "../images/icons/greater-than.png"; // Change to right arrow if opening
      "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png"
    : "/testingLogIn/src/main/resources/static/images/icons/greater-than.png"; // Change to right arrow if opening

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
    .forEach(
      (img) =>
        (img.src =
          "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png")
    );
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

document.addEventListener("DOMContentLoaded", function () {
  function toggleModal(modalId, show = true, message = "") {
    const modal = document.getElementById(modalId);
    if (modal) {
      modal.style.visibility = show ? "visible" : "hidden";
      modal.style.opacity = show ? "1" : "0";

      // If the modal is the confirmation modal, update the text
      if (modalId === "confirmationModal" && message) {
        document.getElementById("modalText").textContent = message;

        // Reset confirm action properly to avoid previous events messing up
        let confirmBtn = document.getElementById("confirmAction");
        let newConfirmBtn = confirmBtn.cloneNode(true);
        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

        // Set new event listener for confirm action
        newConfirmBtn.setAttribute("data-confirm-action", action);
        newConfirmBtn.addEventListener("click", function () {
          handleConfirmAction(action);
          toggleModal("confirmationModal", false);
        });
      }

      if (modalId.includes("Edit") && show) {
        const confirmBtn = modal.querySelector(".btn-confirm");
        const cancelBtn = modal.querySelector(".btn-cancel");
        const inputs = modal.querySelectorAll("input, textarea, select");

        // Reset inputs and selects to readonly/disabled mode
        inputs.forEach((input) => {
          if (input.tagName === "SELECT") {
            input.disabled = true;
          } else {
            input.readOnly = true;
          }
        });

        // Set initial button states
        confirmBtn.textContent = "Edit";
        cancelBtn.textContent = "Close";
        confirmBtn.setAttribute("data-mode", "edit");
      }
    }
  }

  function handleConfirmAction(action) {
    switch (action) {
      case "addStudent":
        alert("Student information submitted!");
        break;
      case "addGradeLevel":
        alert("Grade Level information saved!");
        break;
      case "editGradeLevel":
        alert("Grade Level information updated!");
        break;
      case "addSchoolYear":
        alert("School Year information saved!");
        break;
      case "makeSchoolYearInactive":
        alert("This School Year is now Inactive!");
        break;
      case "makeSchoolYearArchive":
        alert("This School Year is now in Archive");
        break;
      case "makeSchoolYearActive":
        alert("This School Year is now Active");
        break;
      case "addSection":
        alert("Added Section");
        break;
      case "editSection":
        alert("Edit Section");
        break;
      case "addSubject":
        alert("Subject information saved!");
        break;
      case "editSubject":
        alert("Subject information updated!");
        break;
      case "addTeacher":
        alert("Teacher information saved!");
        break;
      case "editTeacher":
        alert("Edit Teacher");
        break;
      default:
        alert("Unknown action: " + action);
    }
  }

  document.body.addEventListener("click", function (event) {
    const target = event.target;

    // Open modal
    if (target.matches("[data-open-modal]")) {
      const modalId = target.getAttribute("data-open-modal");
      const message = target.getAttribute("data-message") || "";
      const action = target.getAttribute("data-action") || ""; // Get action

      document
        .getElementById("confirmAction")
        .setAttribute("data-confirm-action", action); // Store action
      toggleModal(modalId, true, message);
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
      const action = target.getAttribute("data-confirm-action"); // Get correct action
      handleConfirmAction(action); // Call the function to execute the action
      toggleModal("confirmationModal", false);
    }
  });
});
