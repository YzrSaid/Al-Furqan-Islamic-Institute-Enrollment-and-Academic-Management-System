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
      otherArrowIcon.src = "../images/icons/arrow-down.png";
//        otherArrowIcon.src = "../static/images/icons/arrow-down.png";
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);
  arrowIconImg.src = isOpen
      ? "../images/icons/arrow-down.png" // Change back to down arrow if closing
      : "../images/icons/greater-than.png"; // Change to right arrow if opening
//    ? "../static/images/icons/arrow-down.png" // Change back to down arrow if closing
//    : "../static/images/icons/greater-than.png"; // Change to right arrow if opening

  // *🔹 Save submenu state only if it is a submenu of a submenu*
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
    .forEach((img) => (img.src = "../images/icons/arrow-down.png"));
//    .forEach((img) => (img.src = "../static/images/icons/arrow-down.png"));
}

document.addEventListener("DOMContentLoaded", function () {
  // Select all dropdown links inside dropdown-status-content
  const dropdownLinks = document.querySelectorAll(".dropdown-status-content a");
  const modal = document.getElementById("confirmationModal");
  const modalText = document.getElementById("modalText");
  const confirmButton = document.querySelector(".btn-confirm");
  const cancelButton = document.querySelector(".btn-cancel");

  // Hide modal when page loads
  modal.style.display = "none";

  dropdownLinks.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "proceed") {
        modalText.textContent = "Are you sure you want to proceed?";
      } else if (action === "cancel") {
        modalText.textContent = "Are you sure you want to cancel registration?";
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        console.log(action + " confirmed!"); // Add actual functionality here
        modal.style.display = "none"; // Close modal after action
      };
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
    // Student Information Modal
    const studentInfoModal = document.getElementById("studentInfoModal");
    const openStudentModal = document.getElementById("openStudentModal"); 
    const confirmStudent = document.getElementById("confirmStudent");
    const cancelStudent = document.getElementById("cancelStudent");

    if (studentInfoModal) {
        studentInfoModal.classList.remove("show");
    }

    if (openStudentModal) {
        openStudentModal.addEventListener("click", function () {
            studentInfoModal.classList.add("show");
        });
    }

    if (cancelStudent) {
        cancelStudent.addEventListener("click", function () {
            studentInfoModal.classList.remove("show");
        });
    }

    if (confirmStudent) {
        confirmStudent.addEventListener("click", function () {
            alert("Student information submitted!");
            studentInfoModal.classList.remove("show");
        });
    }

    window.addEventListener("click", function (event) {
        if (event.target === studentInfoModal) {
            studentInfoModal.classList.remove("show");
        }
    });

    // Grade Level Modal
    const gradeLevelModal = document.getElementById("gradeLevelModal");
    const openGradeLevelModal = document.getElementById("openGradeLevelModal"); // Button to open modal
    const confirmGradeLevel = document.getElementById("confirmGradeLevel");
    const cancelGradeLevel = document.getElementById("cancelGradeLevel");

    if (gradeLevelModal) {
        gradeLevelModal.classList.remove("show");
    }

    if (openGradeLevelModal) {
        openGradeLevelModal.addEventListener("click", function () {
            gradeLevelModal.classList.add("show");
        });
    }

    if (cancelGradeLevel) {
        cancelGradeLevel.addEventListener("click", function () {
            gradeLevelModal.classList.remove("show");
        });
    }

    if (confirmGradeLevel) {
        confirmGradeLevel.addEventListener("click", function () {
            alert("Grade Level information saved!");
            gradeLevelModal.classList.remove("show");
        });
    }

    window.addEventListener("click", function (event) {
        if (event.target === gradeLevelModal) {
            gradeLevelModal.classList.remove("show");
        }
    });
});

