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

          otherArrowIcon.src = "../images/icons/arrow-down.png";
        // ("/testingLogIn/src/main/resources/static/images/icons/arrow-down.png");
      }
    }
  });

  // Toggle the clicked submenu
  submenu.classList.toggle("open", !isOpen);

  // *🔹 Save submenu state only if it is a submenu of a submenu*
  arrowIconImg.src = isOpen
    ? "../images/icons/arrow-down.png" // Change back to down arrow if closing
    : "../images/icons/greater-than.png"; // Change to right arrow if opening
  //   "/testingLogIn/src/main/resources/static/images/icons/arrow-down.png"
  // : "/testingLogIn/src/main/resources/static/images/icons/greater-than.png"; // Change to right arrow if opening

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
    .forEach((img) => (img.src = "../images/icons/arrow-down.png"));
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

// this is for the maintenance schedule (teacher)
document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("a.view-sched").forEach((link) => {
    link.addEventListener("click", function (event) {
      event.preventDefault(); // Prevent default navigation
      window.location.href = "/sched-board"; // Navigate to the sched board page
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
    let actionUrl;
    let method = "POST"; // Default method

    // Prepare form data
    let formData = new FormData();

    switch (action) {
      case "addGradeLevel":
        actionUrl = "/gradelevel/add";
        method = "POST";
        formData.append(
          "levelName",
          document.getElementById("levelName").value
        );
        break;
      case "editGradeLevel":
        editGradeLevel();
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
      case "verifyAccount":
        alert("Account verified successfully!");
        break;
      case "rejectAccount":
        alert("Account rejected successfully!");
        break;
      case "restrictAccount":
        alert("Account restricted!");
        break;
      case "unrestrictAccount":
        alert("Account unrestricted!");
        break;
      case "editMyAccount":
        alert("Account edited successfully!");
        break;
      case "changePassword":
        alert("Password for this account has been changed successfully!");
        break;
      case "saveSched":
        // code
        alert("Schedule Saved Successfully!");
        break;
      default:
        alert("Unknown action: " + action);
        return;
    }

    // Perform the AJAX request
    fetch(actionUrl, {
      method: method,
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Server response error");
        }
        return response.json();
      })
      .then((data) => {
        alert(data.message || "Action completed successfully!");
        toggleModal("confirmationModal", false);

        // Optional: Refresh page or update UI
        location.reload();
      })
      .catch((error) => {
        alert("Error: " + error.message);
      });
  }

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
      const action = target.getAttribute("data-confirm-action"); // Get correct action
      handleConfirmAction(action); // Call the function to execute the action

      // Close confirmation modal
      toggleModal("confirmationModal", false);

      // Find the parent modal (the Edit Modal) and close it
      const openModal = document.querySelector(
        ".modal[style*='visibility: visible']"
      );
      if (openModal) {
        toggleModal(openModal.id, false);
      }
    }
  });
});

function editGradeLevel() {
  const gradeLevelId = document.getElementById("gradeLevelEditForm").dataset.id;

  const gradeLevelData = {
    id: gradeLevelId, // Ensure the ID is sent
    levelName: document.getElementById("levelName").value,
    enrollees: parseInt(document.getElementById("enrollees-num").value),
    status: document.getElementById("grade-level-status").value,
  };

  fetch("/update-grade-level", {
    // Adjust endpoint if needed
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(gradeLevelData),
  })
    .then((response) => response.text()) // Expecting a text response
    .then((data) => {
      alert(data); // Show the response message
      if (data.includes("Successfully")) {
        location.reload(); // Refresh to reflect the changes
      }
    })
    .catch((error) => console.error("Error:", error));
}

document.addEventListener("DOMContentLoaded", function () {
  fetchGradeLevels(); // Call function when page loads
});

function fetchGradeLevels() {
  fetch("/gradelevel/all")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to fetch grade levels");
      }
      return response.json();
    })
    .then((data) => {
      const tableBody = document.querySelector("#lamesa tbody");
      tableBody.innerHTML = ""; // Clear existing rows

      data.forEach((grade) => {
        const statusText = grade.status ? "Active" : "Inactive";
        const statusColor = grade.status ? "green" : "gray";

        const row = document.createElement("tr");
        row.innerHTML = `
                      <td>${grade.levelName}</td>
                      <td>
                          <div class="status-container">
                              <div class="status" style="background-color: ${statusColor}; font-weight: bold;">
                                  ${statusText}
                              </div>
                          </div>
                      </td>
                      <td>
                          <div class="action-container">
                              <div class="action">
                                  <img data-open-modal="gradeLevelEditModal" 
                                      src="/images/icons/compose.png" 
                                      alt="grade-level-icon" 
                                      onclick="openEditModal(${grade.levelId}, '${grade.levelName}')">
                              </div>
                          </div>
                      </td>
                  `;
        tableBody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

document.addEventListener("click", function (event) {
  const target = event.target;

  if (target.dataset.openModal === "gradeLevelEditModal") {
    const gradeLevelId = target.dataset.id;
    alert(gradeLevelId);
    // Fetch grade level data
    fetch(`/grade-level/${gradeLevelId}`)
      .then((response) => response.json())
      .then((data) => {
        // Populate the modal fields with fetched data
        document.getElementById("gradeLevelEditForm").dataset.id = data.id;
        document.getElementById("levelName").value = data.levelName;
        document.getElementById("grade-level-status").value = data.status;

        // Open the modal
        document.getElementById("gradeLevelEditModal").style.display = "block";
      })
      .catch((error) => console.error("Error fetching grade level:", error));
  }
});

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
    } else if (hasInitialRows) {
      // Set "Edit" only if rows existed when the page loaded
      saveBtn.textContent = "Edit";
      addRowBtn.style.display = "none";
      clearBtn.style.display = "none";
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
                <img class="delete-row" src="../../../static/images/icons/cross.png" alt="Delete" style="display: inline-block; cursor: pointer;">
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

  addRowBtn.addEventListener("click", () => {
    tableBody.appendChild(createRow());
    updateButtonState();
    // Ensure button remains "Save" when adding more rows
    saveBtn.textContent = "Save"; // Force it to remain "Save"
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
