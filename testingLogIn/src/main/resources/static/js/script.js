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

        //   otherArrowIcon.src = "../images/icons/arrow-down.png";
        ("/testingLogIn/src/main/resources/static/images/icons/arrow-down.png");
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);

//   arrowIconImg.src = isOpen
//    "../images/icons/arrow-down.png" // Change back to down arrow if closing
//    : "../images/icons/greater-than.png"; // Change to right arrow if opening
//      "../static/images/icons/arrow-down.png" // Change back to down arrow if closing
//    : "../static/images/icons/greater-than.png"; // Change to right arrow if opening

  // *🔹 Save submenu state only if it is a submenu of a submenu*
  arrowIconImg.src = isOpen ?
         "../images/icons/arrow-down.png" // Change back to down arrow if closing
         : "../images/icons/greater-than.png"; // Change to right arrow if opening
//      "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png"
//    : "/testingLogIn/src/main/resources/static/images/icons/greater-than.png"; // Change to right arrow if opening
  
  // * Save submenu state only if it is a submenu of a submenu*
  
  // * Save submenu state only if it is a submenu of a submenu*

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
    //.forEach((img) => (img.src = "./static/images/icons/arrow-down.png"));
}

document.addEventListener("DOMContentLoaded", function () {
  // Select all dropdown links inside dropdown-status-content
  const dropdownLinks = document.querySelectorAll(".dropdown-status-content a");

  // this is for listing/registration status buttons   
  const dropdownLinksListing = document.querySelectorAll("#dropdown-listing a");

  // this is for school year maintenance
  const dropdownLinksSchoolYear = document.querySelectorAll("#dropdown-school-year a");

  const verificationBtns = document.querySelectorAll(".dropdown-status-content-verify div");

  const modal = document.getElementById("confirmationModal");


  const modalText = document.getElementById("modalText");
  const confirmButton = document.querySelector(".btn-confirm");
  const cancelButton = document.querySelector(".btn-cancel");

  // Hide modal when page loads
  modal.style.display = "none";

  dropdownLinksListing.forEach((link) => {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)
     
      if (action === "proceed-enrollment") {
        modalText.textContent = "Are you sure you want to proceed?";
      } else if (action === "cancel-enrollment") {
      } else if (action === "cancel-enrollment") {
        modalText.textContent = "Are you sure you want to cancel registration?";
      } else {

      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "proceed-enrollment") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "cancel-enrollment"){
            console.log("boogsh")
            // code for reject
        }
        modal.style.display = "none";
      };

      // Handle the cancel button 
      cancelButton.onclick = function () {
        modal.style.display = "none";
      }
  });

  dropdownLinksSchoolYear.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "make-active") {
        modalText.textContent = "Are you sure you want to make this school year active?";
      } else if (action === "make-inactive") {
        modalText.textContent = "Are you sure you want to make this school year inactive?";
      } else if (action === "archive") {
        modalText.textContent = "Are you sure you want to archive this school year? Take note, you cannot access this schoole year once it is already in your archive"
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "proceed-enrollmen") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "cancel-enrolment"){
            console.log("boogsh")
            // code for reject
        }
       
        modal.style.display = "none"; // Close modal after action
      };
      // Handle the cancel button  
      cancelButton.onclick = function () {
        modal.style.display = "none";
      }
    });
  });

  verificationBtns.forEach((link)=> {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "verify") {
        modalText.textContent =
        "Are you sure you want to verify this account?";
      } else if (action === "reject") {
        modalText.textContent =
        "Are you sure you want to reject this account?";
      } else {

      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "proceed-enrollment") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "cancel-enrollment"){
            console.log("boogsh")
            // code for reject
        }
        modal.style.display = "none";
      };

      // Handle the cancel button 
      cancelButton.onclick = function () {
        modal.style.display = "none";
      }
    });
  });

  dropdownLinksSchoolYear.forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "make-active") {
        modalText.textContent = "Are you sure you want to make this school year active?";
      } else if (action === "make-inactive") {
        modalText.textContent = "Are you sure you want to make this school year inactive?";
      } else if (action === "archive") {
        modalText.textContent = "Are you sure you want to archive this school year? Take note, you cannot access this schoole year once it is already in your archive"
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "proceed-enrollmen") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "cancel-enrolment"){
            console.log("boogsh")
            // code for reject
        }
       
        modal.style.display = "none"; // Close modal after action
      };
      // Handle the cancel button  
      cancelButton.onclick = function () {
        modal.style.display = "none";
      }
    });
  });

  verificationBtns.forEach((link)=> {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent the default link behavior

      let action = this.getAttribute("data-action"); // Get the action (Proceed or Cancel)

      if (action === "verify") {
        modalText.textContent =
        "Are you sure you want to verify this account?";
      } else if (action === "reject") {
        modalText.textContent =
        "Are you sure you want to reject this account?";
      }

      modal.style.display = "block"; // Show the modal

      // Handle the confirmation button
      confirmButton.onclick = function () {
        if (action === "verify") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "reject"){
            console.log("boogsh")
            // code for reject
        }
       
        if (action === "verify") {
            console.log(action + " confirmed!");
            // code for verification
        } else if (action === "reject"){
            console.log("boogsh")
            // code for reject
        }
       
        modal.style.display = "none"; // Close modal after action
      };

      cancelButton.onclick = function () {
        console.log(action + " cancel!");
        modal.style.display = "none";
      };

      cancelButton.onclick = function () {
        console.log(action + " cancel!");
        modal.style.display = "none";
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

  // School Year Modal
  const schoolYearModal = document.getElementById("schoolYearModal");
  const openSchoolYearModal = document.getElementById("openSchoolYearModal"); // Button to open modal
  const confirmSchoolYear = document.getElementById("confirmSchoolYear");
  const cancelSchoolYear = document.getElementById("cancelSchoolYear");

  if (schoolYearModal) {
    schoolYearModal.classList.remove("show");
  }

  if (openSchoolYearModal) {
    openSchoolYearModal.addEventListener("click", function () {
      schoolYearModal.classList.add("show");
    });
  }

  if (cancelSchoolYear) {
    cancelSchoolYear.addEventListener("click", function () {
      schoolYearModal.classList.remove("show");
    });
  }

  if (confirmSchoolYear) {
    confirmSchoolYear.addEventListener("click", function () {
      alert("Grade Level information saved!");
      schoolYearModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === schoolYearModal) {
      schoolYearModal.classList.remove("show");
    }
  });

  // Subject Modal
  const subjectModal = document.getElementById("subjectModal");
  const openSubjectModal = document.getElementById("openSubjectModal"); // Button to open modal
  const confirmSubject = document.getElementById("confirmSubject");
  const cancelSubject = document.getElementById("cancelSubject");

  if (subjectModal) {
    subjectModal.classList.remove("show");
  }

  if (openSubjectModal) {
    openSubjectModal.addEventListener("click", function () {
      subjectModal.classList.add("show");
    });
  }

  if (cancelSubject) {
    cancelSubject.addEventListener("click", function () {
      subjectModal.classList.remove("show");
    });
  }

  if (confirmSubject) {
    confirmSubject.addEventListener("click", function () {
      alert("Grade Level information saved!");
      subjectModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === subjectModal) {
      subjectModal.classList.remove("show");
    }
  });

  // Teacher Modal
  const teacherModal = document.getElementById("teacherModal");
  const openTeacherModal = document.getElementById("openTeacherModal"); // Button to open modal
  const confirmTeacher = document.getElementById("confirmTeacher");
  const cancelTeacher = document.getElementById("cancelTeacher");

  if (teacherModal) {
    teacherModal.classList.remove("show");
  }

  if (openTeacherModal) {
    openTeacherModal.addEventListener("click", function () {
      teacherModal.classList.add("show");
    });
  }

  if (cancelTeacher) {
    cancelTeacher.addEventListener("click", function () {
      teacherModal.classList.remove("show");
    });
  }

  if (confirmTeacher) {
    confirmTeacher.addEventListener("click", function () {
      alert("Grade Level information saved!");
      teacherModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === teacherModal) {
      teacherModal.classList.remove("show");
    }
  });

  // Section Modal
  const sectionModal = document.getElementById("sectionModal");
  const openSectionModal = document.getElementById("openSectionModal"); // Button to open modal
  const confirmSection = document.getElementById("confirmSection");
  const cancelSection = document.getElementById("cancelSection");

  if (sectionModal) {
    sectionModal.classList.remove("show");
  }

  if (openSectionModal) {
    openSectionModal.addEventListener("click", function () {
      sectionModal.classList.add("show");
    });
  }

  if (cancelSection) {
    cancelSection.addEventListener("click", function () {
      sectionModal.classList.remove("show");
    });
  }

  if (confirmSection) {
    confirmSection.addEventListener("click", function () {
      alert("Grade Level information saved!");
      sectionModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === sectionModal) {
      sectionModal.classList.remove("show");
    }
  });
});
