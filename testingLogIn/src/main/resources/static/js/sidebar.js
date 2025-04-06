document.addEventListener("DOMContentLoaded", () => {
    const currentPath = window.location.pathname;
  
    // Highlight main menu link
    document.querySelectorAll(".sidebar-icons").forEach((link) => {
      const linkPath = link.getAttribute("data-path");
  
      if (currentPath.startsWith(linkPath)) {
        link.classList.add("active");
  
        const submenu = link.nextElementSibling;
        if (submenu && submenu.classList.contains("submenu")) {
          submenu.classList.add("show");
        }
      }
    });
  
    // Highlight submenu link or view icon
    document.querySelectorAll(".submenu-item").forEach((subLink) => {
      const subLinkPath = subLink.getAttribute("data-path");
  
      if (currentPath.startsWith(subLinkPath)) {
        subLink.classList.add("second-active");
  
        const parentSubmenu = subLink.closest(".submenu");
        if (parentSubmenu) {
          parentSubmenu.classList.add("show");
  
          const parentLink = parentSubmenu.previousElementSibling;
          if (parentLink) {
            parentLink.classList.add("active");
          }
        }
      }
    });
  });
  
  

// New breadcrumb logic
function updateBreadcrumb(path) {
  const breadcrumbContainer = document.querySelector(".text-link");
  breadcrumbContainer.innerHTML = ''; // Clear previous breadcrumb

  // Define breadcrumb items dynamically
  const breadcrumbItems = path.split('/').filter(item => item.length > 0);

  let breadcrumbHTML = `<h4>${breadcrumbItems[breadcrumbItems.length - 1]}</h4>`;
  breadcrumbItems.forEach((item, index) => {
    if (index !== 0) breadcrumbHTML += `<p>/</p>`;
    breadcrumbHTML += `<p><a href="${path.substring(0, path.indexOf(item) + item.length)}">${item.charAt(0).toUpperCase() + item.slice(1)}</a></p>`;
  });

  breadcrumbContainer.innerHTML = breadcrumbHTML;
}

// Update the breadcrumb on page load based on the current URL
updateBreadcrumb(currentPath);

// Add click event listeners to sidebar links to update the breadcrumb when clicked
document.querySelectorAll('.sidebar-icons, .submenu-item').forEach(function (link) {
  link.addEventListener('click', function (event) {
    event.preventDefault();
    const path = link.getAttribute('data-path');
    updateBreadcrumb(path);
    window.location.href = path;
  });
});
