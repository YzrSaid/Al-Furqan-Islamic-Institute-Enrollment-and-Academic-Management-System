document.addEventListener("DOMContentLoaded", function () {
  let savedSubmenus = JSON.parse(localStorage.getItem("openedSubmenus")) || [];

  savedSubmenus.forEach((submenuId) => {
    let submenu = document.getElementById(submenuId);
    if (submenu) {
      submenu.classList.add("open");

      // Make sure parent menus are also opened
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
    }
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const currentPath = window.location.pathname;

  // Remove all active classes before adding the current one
  document.querySelectorAll(".sidebar-icons, .submenu-item").forEach((link) => {
    link.classList.remove("active", "second-active");
  });

  // Highlight main menu link
  document.querySelectorAll(".sidebar-icons").forEach((link) => {
    const linkPath = link.getAttribute("data-path");

    if (currentPath.startsWith(linkPath)) {
      link.classList.add("active"); // Highlight main menu link

      // Open submenu if the current path matches
      const submenu = link.nextElementSibling;
      if (submenu && submenu.classList.contains("submenu")) {
        submenu.classList.add("show");
      }
    }
  });

  // Highlight submenu link
  document.querySelectorAll(".submenu-item").forEach((subLink) => {
    const subLinkPath = subLink.getAttribute("data-path");

    if (currentPath === subLinkPath) {
      subLink.classList.add("second-active"); // Highlight submenu link

      // Open the parent submenu
      const parentSubmenu = subLink.closest(".submenu");
      if (parentSubmenu) {
        parentSubmenu.classList.add("show");

        // Highlight the main menu link
        const parentLink = parentSubmenu.previousElementSibling;
        if (parentLink) {
          parentLink.classList.add("active");
        }
      }
    }
  });
});
