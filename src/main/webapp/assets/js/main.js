// CarCareBooking - Global Script
document.addEventListener('DOMContentLoaded', function () {
    // Tô sáng mục menu đang mở: khớp chính xác đường dẫn (bỏ qua query string)
    var currentPath = window.location.pathname.replace(/\/+$/, '');
    document.querySelectorAll('.sidebar .nav-link').forEach(function (link) {
        var href = link.getAttribute('href');
        if (!href) return;
        var linkPath = href.split('?')[0].replace(/\/+$/, '');
        if (currentPath === linkPath) {
            link.classList.add('active');
            link.setAttribute('aria-current', 'page');
        }
    });
});
