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