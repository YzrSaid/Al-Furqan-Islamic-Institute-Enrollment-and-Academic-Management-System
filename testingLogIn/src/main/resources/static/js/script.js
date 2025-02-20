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

  // this is for manage acounts dropdown
  const dropdownLinksManageAccounts = document.querySelectorAll(
    "#dropdown-manage-accounts a"
  );

  const modal = document.getElementById("confirmationModal");

  const modalText = document.getElementById("modalText");
  const confirmButton = document.querySelector(".btn-confirm");
  const cancelButton = document.querySelector(".btn-cancel");

  // Hide modal when page loads
  modal.style.display = "none";

  //this is for listing/registration status buttonns
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
      // code for saving grade level information

      alert("Grade Level information saved!");
      gradeLevelModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === gradeLevelModal) {
      gradeLevelModal.classList.remove("show");
    }
  });

  // Grade Level Edit Modal
  const gradeLevelEditModal = document.getElementById("gradeLevelEditModal");
  const openGradeLevelEditModal = document.getElementById(
    "openGradeLevelEditModal"
  ); // Button to open modal
  const confirmEditGradeLevel = document.getElementById(
    "confirmEditGradeLevel"
  );
  const cancelEditGradeLevel = document.getElementById("cancelEditGradeLevel");

  if (gradeLevelEditModal) {
    gradeLevelEditModal.classList.remove("show");
  }

  if (openGradeLevelEditModal) {
    openGradeLevelEditModal.addEventListener("click", function () {
      gradeLevelEditModal.classList.add("show");
    });
  }

  if (cancelEditGradeLevel) {
    cancelEditGradeLevel.addEventListener("click", function () {
      cancelEditGradeLevel.textContent = "Close";
      confirmEditGradeLevel.textContent = "Edit";
      gradeLevelEditModal.classList.remove("show");
    });
  }

  if (confirmEditGradeLevel) {
    confirmEditGradeLevel.addEventListener("click", function () {
      if (confirmEditGradeLevel.textContent === "Edit") {
        confirmEditGradeLevel.textContent = "Update";
        cancelEditGradeLevel.textContent = "Cancel";
        // code for editing grade level information
      } else if (confirmEditGradeLevel.textContent === "Update") {
        // code for updating grade level information
        alert("Grade Level information saved!");
        gradeLevelEditModal.classList.remove("show");
        confirmEditGradeLevel.textContent = "Edit";
        cancelEditGradeLevel.textContent = "Close";
      }
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === gradeLevelModal) {
      gradeLevelEditModal.classList.remove("show");
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
      // code for saving/adding school year

      alert("School Year information saved!");
      schoolYearModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === schoolYearModal) {
      schoolYearModal.classList.remove("show");
    }
  });

  // School Year Edit Modal
  const schoolYearEditModal = document.getElementById("schoolYearEditModal");
  const openSchoolYearEditModal = document.getElementById(
    "openSchoolYearEditModal"
  ); // Button to open modal
  const confirmEditSchoolYear = document.getElementById(
    "confirmEditSchoolYear"
  );
  const cancelEditSchoolYear = document.getElementById("cancelEditSchoolYear");

  if (schoolYearEditModal) {
    schoolYearEditModal.classList.remove("show");
  }

  if (openSchoolYearEditModal) {
    openSchoolYearEditModal.addEventListener("click", function () {
      schoolYearEditModal.classList.add("show");
    });
  }

  if (cancelEditSchoolYear) {
    cancelEditSchoolYear.addEventListener("click", function () {
      cancelEditSection.textContent = "Close";
      confirmEditSection.textContent = "Edit";
      sectionEditModal.classList.remove("show");
    });
  }

  if (confirmEditSchoolYear) {
    confirmEditSchoolYear.addEventListener("click", function () {
      // code for saving/adding school year
      if (confirmEditSchoolYear.textContent === "Edit") {
        confirmEditSection.textContent = "Update";
        cancelEditSection.textContent = "Cancel";
        // code for editing school year information
      } else if (confirmEditSection.textContent === "Update") {
        // code for updating school year information
        alert("School Year information saved!");
        schoolYearEditModal.classList.remove("show");
        confirmEditSchoolYear.textContent = "Edit";
        cancelEditSchoolYear.textContent = "Close";
      }

      alert("School Year information saved!");
      schoolYearEditModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === schoolYearEditModal) {
      schoolYearEditModal.classList.remove("show");
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
      // code for saving a subject

      alert("Subject information saved!");
      subjectModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === subjectModal) {
      subjectModal.classList.remove("show");
    }
  });

  // Subject Edit Modal
  const subjectEditModal = document.getElementById("subjectEditModal");
  const openSubjectEditModal = document.getElementById("openSubjectEditModal"); // Button to open modal
  const confirmEditSubject = document.getElementById("confirmEditSubject");
  const cancelEditSubject = document.getElementById("cancelEditSubject");

  if (subjectEditModal) {
    subjectEditModal.classList.remove("show");
  }

  if (openSubjectEditModal) {
    openSubjectEditModal.addEventListener("click", function () {
      subjectEditModal.classList.add("show");
    });
  }

  if (cancelEditSubject) {
    cancelEditSubject.addEventListener("click", function () {
      confirmEditSubject.textContent = "Edit";
      cancelEditSubject.textContent = "Close"
      subjectEditModal.classList.remove("show");
    });
  }

  if (confirmEditSubject) {
    confirmEditSubject.addEventListener("click", function () {
      // code for saving a subject
      if (confirmEditSubject.textContent === "Edit") {
        confirmEditSubject.textContent = "Update";
        cancelEditSubject.textContent = "Cancel";
        // code for editing grade level information
      } else if (confirmEditSubject.textContent === "Update") {
        // code for updating grade level information
        alert("Subject information saved!");
        subjectEditModal.classList.remove("show");
        confirmEditSubject.textContent = "Edit";
        cancelEditSubject.textContent = "Close";
      }
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === subjectEditModal) {
      subjectEditModal.classList.remove("show");
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
      // code for adding a teacher

      alert("Teacher information saved!");
      teacherModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === teacherModal) {
      teacherModal.classList.remove("show");
    }
  });

  
  // Teacher Edit Modal
  const teacherEditModal = document.getElementById("teacherEditModal");
  const openTeacherEditModal = document.getElementById("openTeacherEditModal"); // Button to open modal
  const confirmEditTeacher = document.getElementById("confirmEditTeacher");
  const cancelEditTeacher = document.getElementById("cancelEditTeacher");

  if (teacherEditModal) {
    teacherEditModal.classList.remove("show");
  }

  if (openTeacherEditModal) {
    openTeacherEditModal.addEventListener("click", function () {
      teacherEditModal.classList.add("show");
    });
  }

  if (cancelEditTeacher) {
    cancelEditTeacher.addEventListener("click", function () {
      confirmEditTeacher.textContent = "Edit";
      cancelEditTeacher.textContent = "Close"
      teacherEditModal.classList.remove("show");
    });
  }

  if (confirmEditTeacher) {
    confirmEditTeacher.addEventListener("click", function () {
      // code for saving a teacher
      if (confirmEditTeacher.textContent === "Edit") {
        confirmEditTeacher.textContent = "Update";
        cancelEditTeacher.textContent = "Cancel";
        // code for editing teacher information


      } else if (confirmEditTeacher.textContent === "Update") {
        // code for updating teacher information


        alert("Teacher information saved!");
        teacherEditModal.classList.remove("show");
        confirmEditTeacher.textContent = "Edit";
        cancelEditTeacher.textContent = "Cancel";
      }
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === teacherEditModal) {
      teacherEditModal.classList.remove("show");
    }
  });

  //   

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
      // code for saving/adding  a section

      alert("Section information saved!");
      sectionModal.classList.remove("show");
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === sectionModal) {
      sectionModal.classList.remove("show");
    }
  });

  

  

  // Section Edit Modal
  const sectionEditModal = document.getElementById("sectionEditModal");
  const openSectionEditModal = document.getElementById("openSectionEditModal"); // Button to open modal
  const confirmEditSection = document.getElementById("confirmEditSection");
  const cancelEditSection = document.getElementById("cancelEditSection");

  if (sectionEditModal) {
    sectionEditModal.classList.remove("show");
  }

  if (openSectionEditModal) {
    openSectionEditModal.addEventListener("click", function () {
      sectionEditModal.classList.add("show");
    });
  }

  if (cancelEditSection) {
    cancelEditSection.addEventListener("click", function () {
      cancelEditSection.textContent = "Close";
      confirmEditSection.textContent = "Edit";
      sectionEditModal.classList.remove("show");
    });
  }

  if (confirmEditSection) {
    confirmEditSection.addEventListener("click", function () {
      if (confirmEditSection.textContent === "Edit") {
        confirmEditSection.textContent = "Update";
        cancelEditSection.textContent = "Cancel";
        // code for editing grade level information



      } else if (confirmEditSection.textContent === "Update") {
        // code for updating grade level information
        alert("Section information saved!");
        sectionEditModal.classList.remove("show");
        confirmEditSection.textContent = "Edit";
        cancelEditSection.textContent = "Close";
      }
    });
  }

  window.addEventListener("click", function (event) {
    if (event.target === sectionEditModal) {
      sectionEditModal.classList.remove("show");
    }
  });
});

function updateStatus(isActive) {
    let statusElement = document.getElementById("my-account-status");

    if (isActive) {
        statusElement.classList.add("active");
        statusElement.classList.remove("inactive");
    } else {
        statusElement.classList.add("inactive");
        statusElement.classList.remove("active");
    }
}

// Example Usage: Set status to active (true) or inactive (false)
updateStatus(true); // Green (Active)
