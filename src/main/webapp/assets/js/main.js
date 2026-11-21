// CarCareBooking - Global Script
document.addEventListener('DOMContentLoaded', function () {
    // Tô sáng mục menu đang mở trong sidebar
    var currentPath = window.location.pathname.replace(/\/+$/, '');
    var currentSearch = window.location.search;
    var currentFull = currentPath + currentSearch;

    var matchedLink = null;
    var exactMatchFound = false;

    document.querySelectorAll('.sidebar .nav-link').forEach(function (link) {
        var href = link.getAttribute('href');
        if (!href) return;

        var linkUrl = new URL(link.href, window.location.origin);
        var linkPath = linkUrl.pathname.replace(/\/+$/, '');
        var linkFull = linkPath + linkUrl.search;

        if (linkFull === currentFull) {
            matchedLink = link;
            exactMatchFound = true;
        } else if (!exactMatchFound && linkPath === currentPath) {
            matchedLink = link;
        }
    });

    if (matchedLink) {
        matchedLink.classList.add('active');
        matchedLink.setAttribute('aria-current', 'page');
    }
});
